package kr.hhplus.be.server.coupon.application.dto;

import kr.hhplus.be.server.coupon.domain.model.UserCoupon;
import kr.hhplus.be.server.coupon.domain.type.UserCouponStatus;

import java.time.LocalDateTime;

/**
 * 유저의 쿠폰 정보를 담는 응답 DTO 클래스
 * Application Layer에서 사용되며,
 * 도메인 객체를 외부에 직접 노출하지 않도록 분리한 계층용 DTO
 */
public record UserCouponDto(
        long userCouponId,
        long couponId,
        long userId,
        long policyId,
        UserCouponStatus status,
        String typeSnapshot,
        Float discountRateSnapshot,
        Long discountAmountSnapshot,
        Long minimumOrderAmountSnapshot,
        int usagePeriodSnapshot,
        LocalDateTime expiredAt
) {
    /**
     * 도메인 모델로부터 DTO로 변환하는 정적 팩토리 메서드
     * @param userCoupon 도메인 객체
     * @return UserCouponDto 객체
     */
    public static UserCouponDto from(UserCoupon userCoupon) {
        return new UserCouponDto(
                userCoupon.getUserCouponId(),
                userCoupon.getCouponId(),
                userCoupon.getUserId(),
                userCoupon.getPolicyId(),
                userCoupon.getStatus(),
                userCoupon.getTypeSnapshot(),
                userCoupon.getDiscountRateSnapshot(),
                userCoupon.getDiscountAmountSnapshot(),
                userCoupon.getMinimumOrderAmountSnapshot(),
                userCoupon.getUsagePeriodSnapshot(),
                userCoupon.getExpiredAt()
        );
    }
}
