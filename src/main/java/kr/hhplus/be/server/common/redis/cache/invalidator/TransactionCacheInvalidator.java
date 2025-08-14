package kr.hhplus.be.server.common.redis.cache.invalidator;

import kr.hhplus.be.server.common.redis.cache.events.TransactionOccurredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionCacheInvalidator {

    private final CacheManager cacheManager;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTxOccurred(TransactionOccurredEvent e) {
        try {
            var cache = cacheManager.getCache("tx:recent");
            if (cache != null) {
                cache.evict(e.userId());
                log.debug("거래 내역 캐시 무효화 완료 - 사용자ID: {}", e.userId());
            } else {
                log.warn("거래 내역 캐시를 찾을 수 없음 - 사용자ID: {}", e.userId());
            }
        } catch (Exception ex) {
            log.error("거래 내역 캐시 무효화 실패 - 사용자ID: {}, 오류: {}", e.userId(), ex.getMessage(), ex);
        }
    }
}
