package kr.hhplus.be.server.coupon.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 쿠폰 발행 정보를 나타내는 JPA 엔티티 클래스
 */
@Getter
@Entity
@Table(name = "coupon_issue")
public class CouponIssueJpaEntity {

    protected CouponIssueJpaEntity() {}

    public CouponIssueJpaEntity(Long couponIssueId, Long policyId, Integer totalIssued, Integer remaining,
                                LocalDateTime issueStartDate, String status, Float discountRateSnapshot,
                                Integer usagePeriodSnapshot, String typeSnapshot) {
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_issue_id")
    private Long couponIssueId;

    @Column(name = "policy_id", nullable = false)
    private Long policyId;

    @Column(name = "total_issued", nullable = false)
    private Integer totalIssued;

    @Column(name = "remaining", nullable = false)
    private Integer remaining;

    @Column(name = "issue_start_date", nullable = false)
    private LocalDateTime issueStartDate;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "discount_rate_snapshot")
    private Float discountRateSnapshot;

    @Column(name = "usage_period_snapshot", nullable = false)
    private Integer usagePeriodSnapshot;

    @Column(name = "type_snapshot", nullable = false, length = 50)
    private String typeSnapshot;
}