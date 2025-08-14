package kr.hhplus.be.server.coupon.application.usecase;

import kr.hhplus.be.server.coupon.application.dto.CouponPolicyDto;

public interface FindCouponPolicyUseCase {
    CouponPolicyDto findById(long couponPolicyId);
}
