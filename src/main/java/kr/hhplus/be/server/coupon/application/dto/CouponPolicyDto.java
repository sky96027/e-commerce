package kr.hhplus.be.server.coupon.application.dto;

import kr.hhplus.be.server.coupon.domain.model.CouponPolicy;
import kr.hhplus.be.server.coupon.domain.model.UserCoupon;
import kr.hhplus.be.server.coupon.domain.type.CouponPolicyStatus;
import kr.hhplus.be.server.coupon.domain.type.CouponPolicyType;
import kr.hhplus.be.server.coupon.domain.type.UserCouponStatus;

import java.time.LocalDateTime;

public record CouponPolicyDto(
        Long policyId,
        Float discountRate,
        int usagePeriod,
        CouponPolicyType type,
        CouponPolicyStatus status
) {
    public static CouponPolicyDto from(CouponPolicy couponPolicy) {
        return new CouponPolicyDto(
                couponPolicy.getPolicyId(),
                couponPolicy.getDiscountRate(),
                couponPolicy.getUsagePeriod(),
                couponPolicy.getType(),
                couponPolicy.getStatus()
        );
    }
}
