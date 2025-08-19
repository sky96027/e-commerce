package kr.hhplus.be.server.common.redis.cache.invalidator;

import kr.hhplus.be.server.common.redis.cache.events.ProductOptionsChangedEvent;
import kr.hhplus.be.server.common.redis.cache.events.ProductUpdatedEvent;
import kr.hhplus.be.server.common.redis.cache.events.StockChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ProductCacheInvalidator {

    private final CacheManager cacheManager;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onProductUpdated(ProductUpdatedEvent e) {
        cacheManager.getCache("product:detail").evict(e.productId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onProductOptionsChanged(ProductOptionsChangedEvent e) {
        cacheManager.getCache("product:options").evict(e.productId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onStockChanged(StockChangedEvent e) {
        // 상품 상세 정보 캐시 무효화 (재고 정보 포함)
        cacheManager.getCache("product:detail").evict(e.productId());
        
        // 상품 옵션 캐시 무효화
        cacheManager.getCache("product:options").evict(e.productId());
        
        // 주문 요약 캐시 무효화 (재고 변경으로 인한 주문 가능 여부 영향)
        cacheManager.getCache("order:summary").clear();
    }
}