package kr.hhplus.be.server.coupon.domain.model;

import kr.hhplus.be.server.coupon.domain.type.CouponIssueStatus;
import kr.hhplus.be.server.coupon.domain.type.CouponPolicyType;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 쿠폰 발급 도메인 모델
 */
@Getter
public class CouponIssue {

    private final long couponIssueId;
    private final long policyId;
    private final int totalIssued;
    private final int remaining;
    private final LocalDateTime issueStartDate;
    private final CouponIssueStatus status;
    private final float discountRateSnapshot;
    private final long discountAmountSnapshot;
    private final long minimumOrderAmountSnapshot;
    private final int usagePeriodSnapshot;
    private final CouponPolicyType typeSnapshot;

    /**
     * 전체 필드를 초기화하는 생성자
     */

    public CouponIssue(
            long couponIssueId,
            long policyId,
            int totalIssued,
            int remaining,
            LocalDateTime issueStartDate,
            CouponIssueStatus status,
            float discountRateSnapshot,
            long discountAmountSnapshot,
            long minimumOrderAmountSnapshot,
            int usagePeriodSnapshot,
            CouponPolicyType typeSnapshot
    ) {
        this.couponIssueId = couponIssueId;
        this.policyId = policyId;
        this.totalIssued = totalIssued;
        this.remaining = remaining;
        this.issueStartDate = issueStartDate;
        this.status = status;
        this.discountRateSnapshot = discountRateSnapshot;
        this.discountAmountSnapshot = discountAmountSnapshot;
        this.minimumOrderAmountSnapshot = minimumOrderAmountSnapshot;
        this.usagePeriodSnapshot = usagePeriodSnapshot;
        this.typeSnapshot = typeSnapshot;
    }

}
