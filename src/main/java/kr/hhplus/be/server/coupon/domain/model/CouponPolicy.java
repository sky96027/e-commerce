package kr.hhplus.be.server.coupon.domain.model;

import kr.hhplus.be.server.coupon.domain.type.CouponPolicyStatus;
import kr.hhplus.be.server.coupon.domain.type.CouponPolicyType;
import lombok.Getter;

/**
 * 쿠폰 정책 도메인 모델
 */
@Getter
public class CouponPolicy {

    private final Long policyId;
    private final Float discountRate;                   // nullable
    private final int usagePeriod;                      // 발급 후 사용 기간(3, 7, 30, 365...)
    private final CouponPolicyType type;                // 할인 유형(RATE, AMOUNT)
    private final CouponPolicyStatus status;            // 정책 사용 여부(ENABLED, DISABLED)

    public CouponPolicy(
            long policyId,
            Float discountRate,
            int usagePeriod,
            CouponPolicyType type,
            CouponPolicyStatus status
    ) {
        this.policyId = policyId;
        this.discountRate = discountRate;
        this.usagePeriod = usagePeriod;
        this.type = type;
        this.status = status;
    }
}