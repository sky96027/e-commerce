package kr.hhplus.be.server.common.redis.lock;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@RequiredArgsConstructor
public class RedisDistributedLockManager {

    private final StringRedisTemplate redis;
    /** unlock 시 DEL + PUBLISH 를 원자적으로 수행하는 스크립트 */
    private final DefaultRedisScript<Long> unlockAndPublishScript;

    private final RedisPubSubWaitRegistry waitRegistry;

    private static final String LOCK_PREFIX = "lock:";
    private static final String CH_SUFFIX   = ":ch";

    /** 락 키 */
    private String k(String key) { return LOCK_PREFIX + key; }
    /** 해제 알림 채널 */
    private String ch(String key) { return LOCK_PREFIX + key + CH_SUFFIX; }

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
                Arrays.asList(k(key), ch(key)), // ← KEYS[1], KEYS[2]
                token                           // ← ARGV[1]
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
        long deadline = System.nanoTime() + wait.toNanos();

        while (true) {
            String token = tryLock(key, ttl);
            if (token != null) return token;

            long remainMs = TimeUnit.NANOSECONDS.toMillis(deadline - System.nanoTime());
            if (remainMs <= 0) return null;

            String channel = ch(key);
            CompletableFuture<String> f = waitRegistry.await(channel);

            // 미스 시그널 가드: 등록 직후 즉시 한 번 더 시도
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
}
