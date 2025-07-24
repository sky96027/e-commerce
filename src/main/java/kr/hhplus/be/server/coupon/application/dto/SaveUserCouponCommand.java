package kr.hhplus.be.server.coupon.application.dto;

import kr.hhplus.be.server.coupon.domain.type.CouponPolicyType;

import java.time.LocalDateTime;

/**
 * 유저 쿠폰 저장 요청용 Command DTO
 */
public record SaveUserCouponCommand(
        long userId,
        long couponId,
        long policyId,
        CouponPolicyType typeSnapshot,
        Float discountRateSnapshot,
        Long discountAmountSnapshot,
        Long minimumOrderAmountSnapshot,
        int usagePeriodSnapshot,
        LocalDateTime expiredAt
) {}