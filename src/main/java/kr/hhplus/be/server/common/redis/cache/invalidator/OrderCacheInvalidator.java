package kr.hhplus.be.server.common.redis.cache.invalidator;

import kr.hhplus.be.server.common.redis.cache.CacheKeyUtil;
import kr.hhplus.be.server.common.redis.cache.events.OrderStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCacheInvalidator {

    private final RedisTemplate<String, Object> redisTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderStatusChanged(OrderStatusChangedEvent e) {
        String key = CacheKeyUtil.orderSummaryKey(e.orderId());
        redisTemplate.delete(key);
        log.debug("주문 요약 캐시 무효화 완료 - key={}", key);
    }
}
