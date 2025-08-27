package kr.hhplus.be.server.common.redis.cache.invalidator;

import kr.hhplus.be.server.common.redis.cache.CacheKeyUtil;
import kr.hhplus.be.server.common.redis.cache.events.CouponPolicyChangedEvent;
import kr.hhplus.be.server.common.redis.cache.events.UserCouponChangedEvent;
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
public class CouponCacheInvalidator {

    private final RedisTemplate<String, Object> redisTemplate;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserCouponChanged(UserCouponChangedEvent e) {
        String key = CacheKeyUtil.couponUserSummaryKey(e.userId());
        redisTemplate.delete(key);
        log.debug("쿠폰 요약 캐시 무효화 완료 - key={}", key);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCouponPolicyChanged(CouponPolicyChangedEvent e) {
        String key = CacheKeyUtil.couponPolicyKey(e.policyId());
        redisTemplate.delete(key);
        log.debug("쿠폰 정책 캐시 무효화 완료 - key={}", key);
    }
}
