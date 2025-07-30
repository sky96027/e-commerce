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
        this.usagePeriodSnapshot = usagePeriodSnapshot;
        this.typeSnapshot = typeSnapshot;
    }

    public CouponIssue decreaseRemaining() {
        if (this.remaining <= 0) {
            throw new IllegalStateException("쿠폰 잔량이 소진되었습니다.");
        }
        return new CouponIssue(
                this.couponIssueId,
                this.policyId,
                this.totalIssued,
                this.remaining - 1,
                this.issueStartDate,
                this.status,
                this.discountRateSnapshot,
                this.usagePeriodSnapshot,
                this.typeSnapshot
        );
    }

}
