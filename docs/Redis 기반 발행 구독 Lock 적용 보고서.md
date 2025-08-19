[Redis 기반 Spin Lock 구현](https://www.notion.so/Redis-Spin-Lock-24a13a3e1bfc80abb9cdfd1a808a8071?pvs=21)

### 기존 Spin Lock 방식의 한계

Spin Lock으로만 동시성을 제어할 경우 트래픽이 클 것으로 예상하는 선착순 쿠폰 발급, 일정 상품에 큰 트래픽을 받을 수 있는 재고 차감 기능에 Spin Lock으로 인한 대기 비용이 커지며 DB에 부하가 크게 걸릴 수 있음.

**대기 비용을 구성하는 요소(용도별)**

- **클라이언트 비용**: 스레드 대기/깨움, 문맥 전환.
- **Redis 비용**: `SET NX`/`GET` 같은 폴링 명령 QPS.
- **지연 비용**: 락이 해제된 후 실제로 잡기까지의 추가 지연.

**스핀락(폴링)의 비용 메커니즘**

- 매 `b`(백오프 간격)마다 `SETNX` 재시도.
- 남은 보유 시간 `L` 동안 한 대기자가 시도하는 횟수 ≈ `ceil(L / b)`.
- 총 Redis 호출 수 (대기자 W명): `≈ W * ceil(L / b)`.
- 해제 후 추가 지연(평균): `≈ b / 2` (해제 시점이 백오프 간격 안에서 균일하다고 가정).
- 경합이 커질수록 `QPS ∝ W / b` 로 증가 → Redis·CPU 낭비.

> 예: b=50ms, L=500ms, W=100
>
> - 시도 횟수/대기자 ≈ 10회 → 총 약 1,000회 `SETNX`
> - 해제 후 평균 추가 지연 ≈ 25ms

**Pub/Sub 락의 비용 메커니즘**

- 실패 시 구독하고 수면. 해제 시점에 서버가 푸시로 깨움.
- 총 Redis 호출 수 :
  - 해제 시 **`PUBLISH`** 1회 + 대기자별 재시도 **`SETNX`**1회 수준.
  - 폴링 명령이 없어져 `QPS`가 경합 시간 **`L`** 에 비례하지 않음.
- 해제 후 추가 지연: `≈ 네트워크 + 리스너 디스패치`(수 ms 수준). `b`에 의존하지 않음.
- 대기 중 스레드는 슬립 상태 → CPU/컨텍스트 스위치 감소.

> 같은 예(L=500ms, W=100)
>
> - 해제 시: `PUBLISH` 1회 + 최대 100회 `SETNX`(재획득)
> - 평균 추가 지연: 수 ms 내외(백오프에 비의존)

### 구현

RedisConfig에 구독 컨테이너 추가, 스크립트 추가

```java
@Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory cf) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(cf);
        return container;
    }
    
    @Bean
    public DefaultRedisScript<Long> unlockAndPublishScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setResultType(Long.class);
        script.setScriptText(
                // KEYS[1]=lockKey, KEYS[2]=channel, ARGV[1]=token
                "local v = redis.call('GET', KEYS[1]); " +
                        "if v == ARGV[1] then " +
                        "  redis.call('DEL', KEYS[1]); " +
                        "  redis.call('PUBLISH', KEYS[2], ARGV[1]); " +
                        "  return 1; " +
                        "else return 0; end"
        );
        return script;
    }
```

대기 레지스트리 신규 작성

```java
package kr.hhplus.be.server.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RequiredArgsConstructor
public class RedisPubSubWaitRegistry implements InitializingBean {

    private final RedisMessageListenerContainer container;

    // 채널별 대기자 리스트
    private final ConcurrentMap<String, CopyOnWriteArrayList<CompletableFuture<String>>> waiters = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() {
        container.addMessageListener((message, pattern) -> {
            String channel = new String(message.getChannel(), java.nio.charset.StandardCharsets.UTF_8);
            String payload = new String(message.getBody(), java.nio.charset.StandardCharsets.UTF_8);

            var list = waiters.remove(channel); // 채널에 걸린 모든 대기자 깨우기
            if (list != null) list.forEach(f -> f.complete(payload));
        }, new org.springframework.data.redis.listener.PatternTopic("lock:*:ch")); // 항상-켜진 pSub
    }

    /** 특정 채널 알림을 기다림 */
    public CompletableFuture<String> await(String channel) {
        var f = new CompletableFuture<String>();
        waiters.compute(channel, (ch, cur) -> {
            if (cur == null) cur = new CopyOnWriteArrayList<>();
            cur.add(f);
            return cur;
        });
        return f;
    }

    /** 타임아웃/취소 시 대기자 제거 */
    public void cancel(String channel, CompletableFuture<String> f) {
        var list = waiters.get(channel);
        if (list != null) {
            list.remove(f);
            if (list.isEmpty()) waiters.remove(channel, list);
        }
    }
}
```

Lock Manager에 Pub/Sub 방식의 Lock 추가

```java
public class RedisDistributedLockManager {

	private final RedisMessageListenerContainer listenerContainer;
	
	public String lockBlockingPubSub(String key, Duration ttl, Duration wait) {
        long deadline = System.nanoTime() + wait.toNanos();

        while (true) {
            String token = tryLock(key, ttl);
            if (token != null) return token;

            long remainMs = TimeUnit.NANOSECONDS.toMillis(deadline - System.nanoTime());
            if (remainMs <= 0) return null;

            String channel = ch(key);
            CompletableFuture<String> f = waitRegistry.await(channel);

            token = tryLock(key, ttl);
            if (token != null) {
                waitRegistry.cancel(channel, f);
                return token;
            }

            try {
                // 알림(PUBLISH) 대기
                f.get(remainMs, TimeUnit.MILLISECONDS);
            } catch (TimeoutException te) {
                return null; // 전체 대기 초과 → 호출부에서 "잠시 후…" 예외
            } catch (Exception ignore) {
                // 인터럽트 등 → 루프 계속
            } finally {
                waitRegistry.cancel(channel, f);
            }
        }
}
```

실제 사용 예시

```java
public void issueToUser(SaveUserCouponCommand command) {
  String key = "coupon:issue:" + command.couponId();

  // [PUB/SUB LOCK] 해제 알림 기반 블로킹 획득
  String token = lockManager.lockBlockingPubSub(
          key,
          Duration.ofSeconds(3),  // TTL (p99 처리시간보다 짧지 않게)
          Duration.ofSeconds(5)   // 전체 대기 한도
  );
  if (token == null) throw new IllegalStateException("잠시 후 다시 시도해 주세요.");

  try {
    saveUserCouponUseCase.save(command);
  } finally {
    lockManager.unlock(key, token);
  }
}
```

### 추가 사항

만약 optionId가 {2, 1, 3}, {1, 2, 3} 인 orderItem 두 개 요청이 동시에 들어왔을 경우 row에 lock이 걸리고 dead lock을 유발할 수 있다.

그래서 아래처럼 결제 로직에 정렬 로직을 추가하였다.

```java
Map<Long, Integer> qtyByOption = new HashMap<>();
for (OrderItemDto item : orderItems) {
        qtyByOption.merge(item.optionId(), item.quantity(), Integer::sum); // 중복 옵션 합치기
        }

// 순서: optionId 오름차순
List<Map.Entry<Long, Integer>> sorted = new ArrayList<>(qtyByOption.entrySet());
sorted.sort(Map.Entry.comparingByKey());
```