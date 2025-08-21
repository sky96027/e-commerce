package kr.hhplus.be.server.coupon.application.usecase;

import kr.hhplus.be.server.coupon.domain.model.CouponPolicy;

public interface CouponPolicyManagementUseCase {
    CouponPolicy updateCouponPolicy(CouponPolicy couponPolicy);
}
