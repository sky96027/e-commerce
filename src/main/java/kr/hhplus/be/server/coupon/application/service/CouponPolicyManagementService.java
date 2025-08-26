package kr.hhplus.be.server.coupon.application.service;

import kr.hhplus.be.server.common.cache.events.CouponPolicyChangedEvent;
import kr.hhplus.be.server.coupon.application.usecase.CouponPolicyManagementUseCase;
import kr.hhplus.be.server.coupon.domain.model.CouponPolicy;
import kr.hhplus.be.server.coupon.domain.repository.CouponPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 쿠폰 정책 관리 서비스
 * 쿠폰 정책 수정 시 관련 캐시를 무효화하는 이벤트를 발행
 */
@Service
@RequiredArgsConstructor
public class CouponPolicyManagementService implements CouponPolicyManagementUseCase {

    private final CouponPolicyRepository couponPolicyRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 쿠폰 정책 수정
     * 변경 후 CouponPolicyChangedEvent를 발행하여 관련 캐시를 무효화
     */
    @Transactional
    public CouponPolicy updateCouponPolicy(CouponPolicy couponPolicy) {
        CouponPolicy updatedPolicy = couponPolicyRepository.update(couponPolicy);

        // 쿠폰 정책 변경 이벤트 발행 (트랜잭션 커밋 후 캐시 무효화)
        eventPublisher.publishEvent(new CouponPolicyChangedEvent(updatedPolicy.getPolicyId()));

        return updatedPolicy;
    }
}
