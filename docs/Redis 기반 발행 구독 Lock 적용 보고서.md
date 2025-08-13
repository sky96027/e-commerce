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
    - 해제 시 **`PUBLISH`** 1회 + 대기자별 재시도 **`SETNX`**1회 수준(미스 시그널 가드 포함해도 상수 회수).
    - 폴링 명령이 없어져 `QPS`가 경합 시간 **`L`** 에 비례하지 않음.
- 해제 후 추가 지연: `≈ 네트워크 + 리스너 디스패치`(수 ms 수준). `b`에 의존하지 않음.
- 대기 중 스레드는 슬립 상태 → CPU/컨텍스트 스위치 감소.

> 같은 예(L=500ms, W=100)
>
> - 해제 시: `PUBLISH` 1회 + 최대 100회 `SETNX`(재획득)
> - 평균 추가 지연: 수 ms 내외(백오프에 비의존)

### 구현

RedisConfig에 구독 컨테이너 추가

```java
@Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory cf) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(cf);
        return container;
    }
```

Lock Manager에 Pub/Sub 방식의 Lock 추가

```java
public class RedisDistributedLockManager {

	private final RedisMessageListenerContainer listenerContainer;
	
	public String lockBlockingPubSub(String key, Duration ttl, Duration wait) {
        long deadlineMillis = System.currentTimeMillis() + wait.toMillis();
        final String channel = ch(key);

        while (System.currentTimeMillis() < deadlineMillis) {
            // 1) 즉시 획득 시도
            String token = tryLock(key, ttl);
            if (token != null) return token;

            // 2) 채널 구독 준비
            CountDownLatch latch = new CountDownLatch(1);
            MessageListener listener = (message, pattern) -> latch.countDown();
            ChannelTopic topic = new ChannelTopic(channel);

            // 3) 구독 등록
            listenerContainer.addMessageListener(listener, topic);
            try {
                // 3-1) 미스 시그널 가드: 구독 직후 락이 이미 사라졌으면 즉시 재시도
                Boolean exists = redis.hasKey(k(key));
                if (Boolean.FALSE.equals(exists)) {
                    // 잠깐 양보(쓰레드 스케줄링) 후 루프 재진입
                    Thread.yield();
                } else {
                    // 4) 남은 시간 내에서 알림 대기 (최대 1초 단위로 끊어서 대기)
                    long remaining = Math.max(0, deadlineMillis - System.currentTimeMillis());
                    long waitOnce = Math.min(remaining, 1000L);
                    if (waitOnce > 0) latch.await(waitOnce, TimeUnit.MILLISECONDS);
                }
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            } finally {
                // 5) 구독 해제
                listenerContainer.removeMessageListener(listener, topic);
            }
            // 루프 재시도
        }
        return null;
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