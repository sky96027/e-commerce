package kr.hhplus.be.server.common.redis.cache;

import java.util.Objects;
import java.util.concurrent.Callable;

import kr.hhplus.be.server.common.redis.lock.RedisDistributedLockManager;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * 캐시 미스 순간 스탬피드 방지를 위한 single-flight 헬퍼.
 * - 분산락: mutex:{cacheName}:{key}
 * - 잠금 획득 후 Cache.get(key, loader)로 원자 로드/저장
 */
@Component
@RequiredArgsConstructor
public class CacheSingleFlight {

    private final CacheManager cacheManager;
    private final RedisDistributedLockManager lockManager;

    public <T> T getOrLoad(@NonNull String cacheName,
                           @NonNull Object key,
                           @NonNull Callable<T> loader) {
        Cache cache = Objects.requireNonNull(cacheManager.getCache(cacheName),
                () -> "Cache not found: " + cacheName);

        // 1) 빠른 히트 경로
        Cache.ValueWrapper vw = cache.get(key);
        if (vw != null) {
            @SuppressWarnings("unchecked")
            T hit = (T) vw.get();
            return hit;
        }

        String mutexKey = "mutex:" + cacheName + ":" + key;
        // 분산락: TTL 5s, 대기 3s (업무 P99에 맞춰 조정)
        String token = lockManager.lockBlockingPubSub(mutexKey,
                java.time.Duration.ofSeconds(5),
                java.time.Duration.ofSeconds(3));
        try {
            // 2) 잠금 보유 중 재확인 + 원자 로드/저장
            return cache.get(key, loader);
        } finally {
            lockManager.unlock(mutexKey, token);
        }
    }
}
