package kr.hhplus.be.server.coupon.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;

/**
 * 쿠폰 정책 정보를 나타내는 JPA 엔티티 클래스
 */
@Getter
@Entity
@Table(name = "coupon_policy")
public class CouponPolicyJpaEntity {

    protected CouponPolicyJpaEntity() {}

    public CouponPolicyJpaEntity(Long policyId, Float discountRate, Integer usagePeriod, String type, String status) {
        this.policyId = policyId;
        this.discountRate = discountRate;
        this.usagePeriod = usagePeriod;
        this.type = type;
        this.status = status;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policy_id")
    private Long policyId;

    @Column(name = "discount_rate")
    private Float discountRate;

    @Column(name = "usage_period", nullable = false)
    private Integer usagePeriod;

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Column(name = "status", nullable = false, length = 50)
    private String status;
}