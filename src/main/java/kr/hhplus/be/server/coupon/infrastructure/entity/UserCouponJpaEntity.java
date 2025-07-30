package kr.hhplus.be.server.coupon.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 사용자 쿠폰 정보를 나타내는 JPA 엔티티 클래스
 */
@Getter
@Entity
@Table(name = "USER_COUPON")
public class UserCouponJpaEntity {

    protected UserCouponJpaEntity() {}

    public UserCouponJpaEntity(Long userCouponId, Long couponId, Long userId, Long policyId, String status,
                               String typeSnapshot, Float discountRateSnapshot, Integer usagePeriodSnapshot,
                               LocalDateTime expiredAt) {
        this.userCouponId = userCouponId;
        this.couponId = couponId;
        this.userId = userId;
        this.policyId = policyId;
        this.status = status;
        this.typeSnapshot = typeSnapshot;
        this.discountRateSnapshot = discountRateSnapshot;
        this.usagePeriodSnapshot = usagePeriodSnapshot;
        this.expiredAt = expiredAt;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_coupon_id")
    private Long userCouponId;

    @Column(name = "coupon_id", nullable = false)
    private Long couponId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "policy_id", nullable = false)
    private Long policyId;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "type_snapshot", nullable = false, length = 50)
    private String typeSnapshot;

    @Column(name = "discount_rate_snapshot")
    private Float discountRateSnapshot;

    @Column(name = "usage_period_snapshot", nullable = false)
    private Integer usagePeriodSnapshot;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;
}