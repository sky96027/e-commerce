### 기존 구조

Facade에서 UseCase 호출하여 orchestration
Transaction도 Facade에서 묶음
Lock은 DB 비관적 락으로 처리하던 구조

[동시성 문제 해결 방안](https://www.notion.so/24513a3e1bfc805c9352d7dc62f07a83?pvs=21)

```java
@Component
public class UserFacade {

usecase 
...
...

생성자
...
...

    @Transactional
    public UserDto chargeWithHistory(long userId, long amount) {
        UserDto updated = chargeUseCase.charge(userId, amount);
        saveTransactionUseCase.save(userId, TransactionType.CHARGE, amount);
        return updated;
    }
```

이후 해당 구조의 한계를 파악하고 성능 개선 및 DB 부하 감소를 고려해 redis Spin Lock 구조로 변경

---

### 리팩토링 계획

```
user/application
	├─ dto/
  ├─ facade/      // 분산락·흐름 제어 (비트랜잭션)
  ├─ usecase/     // Port-in 인터페이스
  └─ service/     // 단일 UC 구현만
			  └─ tx/          // 오케스트레이터만(@Transactional) ****추가****
  
```

트랜잭션 오케스트레이터의 역할을 facade에서 tx로 변경

facade는 상위 오케스트레이터로 분산락/멱등키/레이트리밋 등 흐름 제어

**이유 및 장점**

1. 커밋 전에 락이 풀리는 것을 막기 위해

   facade에 `@Transactional`이 있는 현재 구조로 redis를 적용할 경우 `finally { unlock() }` 가 메서드 반환 전에 실행되고 그 후에 트랜잭션이 커밋됨. 즉 언락 → 커밋 순서가 되어 레이스 발생

2. 임계 구역 최소화

   락을 잡은 상태에서 네트워크 및 로직이 복잡해 길어지면 TTL 이슈가 발생할 수 있음. facade에서 락만 잡고 실제 작업은 tx 서비스에서 빠르게 커밋해 락 홀드 시간을 짧게 만들기 위함

3. SRP 명확화

   facade = 흐름/분산 동기화, Tx 서비스 = 트랜잭션 경계로 역할이 다시한번 쪼개지며 테스트와 교체, 장애 복구가 쉬워짐.


**단점**

1. 패키지가 늘어남.

**구조를 바꾸지 않을 경우 예상**

커밋 이후 언락을 예약해야 함 → 코드 복잡도가 올라가고 실수 여지가 커져 위험할 수 있음.

---

### 구현

**종속성 추가**

```xml
// Redis
	implementation ("org.springframework.boot:spring-boot-starter-data-redis")
	implementation ("org.springframework.boot:spring-boot-starter-cache")
	testImplementation ("org.springframework.boot:spring-boot-starter-test")
```

**Redis 설정 클래스 추가**

```java
@Configuration
@EnableCaching
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private String port;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, Integer.parseInt(port));
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }
    
    @Bean
    public DefaultRedisScript<Long> unlockScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setResultType(Long.class);
        script.setScriptText(
                "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                        "  return redis.call('del', KEYS[1]) " +
                        "else " +
                        "  return 0 " +
                        "end"
        );
        return script;
    }
}
```

**LockManager 추가**

```java
@Component
@RequiredArgsConstructor
public class RedisDistributedLockManager {

    private final StringRedisTemplate redis;
    private final DefaultRedisScript<Long> unlockScript;

    private static final String LOCK_PREFIX = "lock:";

    /** 락 키 네임스페이스 정리용 */
    private String k(String key) {
        return LOCK_PREFIX + key;
    }

    /**
     * 락 시도 (논블로킹): 성공 시 토큰 반환, 실패 시 null
     * - SET key value NX PX ttlMillis
     */
    public String tryLock(String key, Duration ttl) {
        String token = UUID.randomUUID().toString();
        Boolean ok = redis.opsForValue().setIfAbsent(k(key), token, ttl);
        return Boolean.TRUE.equals(ok) ? token : null;
    }

    /**
     * 락 해제: 토큰이 일치할 때만 삭제(Lua로 원자적 보장)
     */
    public boolean unlock(String key, String token) {
        if (token == null) return false;
        Long res = redis.execute(unlockScript, Collections.singletonList(k(key)), token);
        return Objects.equals(res, 1L);
    }

    /**
     * 블로킹 방식: 최대 대기시간 동안 락을 재시도
     */
    public String lockBlocking(String key, Duration ttl, Duration wait, Duration backoff) {
        long deadline = System.nanoTime() + wait.toNanos();
        while (System.nanoTime() < deadline) {
            String token = tryLock(key, ttl);
            if (token != null) return token;
            try { Thread.sleep(backoff.toMillis()); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
        }
        return null;
    }

    /**
     * 편의 메서드: 락 잡고 작업 실행 (성공 시 결과 반환)
     * - 실패 시 IllegalStateException 던짐(필요 시 커스텀 예외로 교체)
     */
    public <T> T executeWithLock(String key, Duration ttl, Supplier<T> action) {
        String token = tryLock(key, ttl);
        if (token == null) throw new IllegalStateException("Lock acquisition failed for key=" + key);
        try {
            return action.get();
        } finally {
            try { unlock(key, token); } catch (DataAccessException ignored) { /* 로그 정도 */ }
        }
    }
}

```

**tx service Port-in 추가**

```java
package kr.hhplus.be.server.user.application.usecase;

import kr.hhplus.be.server.user.application.dto.UserDto;

public interface ChargeUserBalanceWithHistoryUseCase {
    UserDto execute(long userId, long amount);
}
```

**구현체 추가**

```java
@Service
@RequiredArgsConstructor
public class ChargeUserBalanceWithHistoryTxService implements ChargeUserBalanceWithHistoryUseCase {

    private final ChargeUserBalanceUseCase chargeUserBalanceUseCase;
    private final SaveTransactionUseCase saveTransactionUseCase;      // 타 도메인 UseCase

    @Override
    @Transactional
    public UserDto execute(long userId, long amount) {
        UserDto updated = chargeUserBalanceUseCase.charge(userId, amount);
        saveTransactionUseCase.save(userId, TransactionType.CHARGE, amount);
        return updated;
    }
}

```

**Repository, RepositoryImpl 수정**

```java
@Override
    public User charge(long userId, long amount) {
        int updated = jpaRepository.incrementBalance(userId, amount);
        if (updated != 1) {
            throw new IllegalStateException("충전 실패: userId=" + userId);
        }
        UserJpaEntity entity = jpaRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found: " + userId));
        return mapper.toDomain(entity);
    }
```

**JpaRepository 수정**

```java
@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
    // 비관적 락 (legacy)
    /*@Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM UserJpaEntity u WHERE uuserId = :id")
    Optional<UserJpaEntity> findByIdForUpdate(@Param("id") Long id);*/

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE UserJpaEntity u " +
            "   SET u.balance = u.balance + :amount " +
            "WHERE u.userId = :id")
    int incrementBalance(@Param("id") Long id, @Param("amount") Long amount);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE UserJpaEntity u " +
            "   SET u.balance = u.balance - :amount " +
            "WHERE u.userId = :id " +
            "   AND (u.balance - :amount) >= 0")
    int decrementBalanceIfEnough(@Param("id") Long id, @Param("amount") Long amount);

}
```

리팩토링을 진행하며 facade에 존재하던 `@Transactional` 은 txService로 이동함.
user domain model 에서 처리하던 차감, 충전 규칙 책임을 JPA 원자 UPDATE가져가면서 domain model은 입력 검증만을 책임짐.

### 결과 및 주의 사항

기존에 존재하던 charge concurrency test (50개 동시 요청)이 2.19 초로 통과함.
트래픽이 커진다는 가정에서 ttl을 증가시켜야할 수 있음. (현재 3초)

이미 Update에서 원자적으로 진행하는데 Lock이 필요할까? 에 대한 고민 필요

redis 기반 분산 락 공부를 위해 선착순 쿠폰과 상품 재고 차감 로직에도 적용시켰으나
트래픽이 클 것으로 예상되는 선착순 쿠폰은 Redis 세마포어/Lua 스크립트 방식,
알 수 없는 트래픽량의 상품 재고 차감은 DB의 조건부 원자 업데이트 + 낙관적 재시도 + kafka 큐
방식이 적절해 보임 → 공부 후 적용