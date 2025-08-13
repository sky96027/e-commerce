package kr.hhplus.be.server.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

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
