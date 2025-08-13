package kr.hhplus.be.server.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class RedisDistributedLockManager {

    private final StringRedisTemplate redis;
    /** unlock 시 DEL + PUBLISH 를 원자적으로 수행하는 스크립트 */
    private final DefaultRedisScript<Long> unlockAndPublishScript;
    /** Pub/Sub 구독 관리 컨테이너 */
    private final RedisMessageListenerContainer listenerContainer;

    private static final String LOCK_PREFIX   = "lock:";
    private static final String NOTIFY_PREFIX = "lock:notify:";

    /** 락 키 */
    private String k(String key) { return LOCK_PREFIX + key; }
    /** 해제 알림 채널 */
    private String ch(String key) { return NOTIFY_PREFIX + key; }

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
     * 락 해제: 토큰이 일치할 때만 삭제 + 해제 알림(PUBLISH)
     * - Lua 로 원자 보장(DEL & PUBLISH)
     */
    public boolean unlock(String key, String token) {
        if (token == null) return false;
        Long res = redis.execute(
                unlockAndPublishScript,
                java.util.Arrays.asList(k(key), ch(key)),
                token
        );
        return Objects.equals(res, 1L);
    }

    /**
     * [SPIN LOCK] 블로킹 방식(폴링): 최대 대기시간 동안 backoff 간격으로 재시도
     * - 경쟁이 높으면 Redis 트래픽 증가 가능
     */
    public String lockBlocking(String key, Duration ttl, Duration wait, Duration backoff) {
        long deadline = System.nanoTime() + wait.toNanos();
        while (System.nanoTime() < deadline) {
            String token = tryLock(key, ttl);
            if (token != null) return token;
            try { Thread.sleep(backoff.toMillis()); }
            catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
        }
        return null;
    }

    /**
     * [PUB/SUB LOCK] 블로킹 방식(해제 알림 대기):
     * - 실패 시 채널 구독 후 알림을 받으면 즉시 재시도
     * - 미스 시그널 방지: "구독 직후 락 존재 여부 재확인" 포함
     */
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
