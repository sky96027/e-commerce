package kr.hhplus.be.server.common.redis.cache.invalidator;

import kr.hhplus.be.server.common.redis.cache.events.CouponPolicyChangedEvent;
import kr.hhplus.be.server.common.redis.cache.events.UserCouponChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CouponCacheInvalidator {

    private final CacheManager cacheManager;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserCouponChanged(UserCouponChangedEvent e) {
        cacheManager.getCache("coupon:userSummary").evict(e.userId());
    }
    
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCouponPolicyChanged(CouponPolicyChangedEvent e) {
        cacheManager.getCache("coupon:policy").evict(e.policyId());
    }
}
