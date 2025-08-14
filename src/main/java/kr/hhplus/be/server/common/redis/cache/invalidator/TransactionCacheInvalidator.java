package kr.hhplus.be.server.common.redis.cache.invalidator;

import kr.hhplus.be.server.common.redis.cache.events.TransactionOccurredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class TransactionCacheInvalidator {

    private final CacheManager cacheManager;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTxOccurred(TransactionOccurredEvent e) {
        cacheManager.getCache("tx:recent").evict(e.userId());
    }
}
