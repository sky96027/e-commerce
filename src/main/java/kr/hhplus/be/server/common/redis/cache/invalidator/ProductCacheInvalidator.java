package kr.hhplus.be.server.common.redis.cache.invalidator;

import kr.hhplus.be.server.common.redis.cache.CacheKeyUtil;
import kr.hhplus.be.server.common.redis.cache.events.ProductOptionsChangedEvent;
import kr.hhplus.be.server.common.redis.cache.events.ProductUpdatedEvent;
import kr.hhplus.be.server.common.redis.cache.events.StockChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductCacheInvalidator {

    private final RedisTemplate<String, Object> redisTemplate;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onProductUpdated(ProductUpdatedEvent e) {
        String key = CacheKeyUtil.productDetailKey(e.productId());
        redisTemplate.delete(key);
        log.debug("상품 상세 캐시 무효화 - key={}", key);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onProductOptionsChanged(ProductOptionsChangedEvent e) {
        String key = CacheKeyUtil.productOptionsKey(e.productId());
        redisTemplate.delete(key);
        log.debug("상품 옵션 캐시 무효화 - key={}", key);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onStockChanged(StockChangedEvent e) {
        // 상품 상세 정보 캐시 무효화 (재고 정보 포함)
        String detailKey = CacheKeyUtil.productDetailKey(e.productId());
        redisTemplate.delete(detailKey);
        log.debug("재고 변경 → 상품 상세 캐시 무효화 - key={}", detailKey);

        // 상품 옵션 캐시 무효화
        String optionsKey = CacheKeyUtil.productOptionsKey(e.productId());
        redisTemplate.delete(optionsKey);
        log.debug("재고 변경 → 상품 옵션 캐시 무효화 - key={}", optionsKey);
    }
}