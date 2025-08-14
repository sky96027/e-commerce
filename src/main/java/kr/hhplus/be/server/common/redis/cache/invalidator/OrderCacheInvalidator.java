package kr.hhplus.be.server.common.redis.cache.invalidator;

import kr.hhplus.be.server.common.redis.cache.events.OrderStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderCacheInvalidator {

    private final CacheManager cacheManager;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderStatusChanged(OrderStatusChangedEvent e) {
        cacheManager.getCache("order:summary").evict(e.orderId());
    }
}
