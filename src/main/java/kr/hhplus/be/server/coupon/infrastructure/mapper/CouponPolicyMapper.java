package kr.hhplus.be.server.coupon.infrastructure.mapper;

import kr.hhplus.be.server.coupon.domain.model.CouponPolicy;
import kr.hhplus.be.server.coupon.domain.type.CouponPolicyStatus;
import kr.hhplus.be.server.coupon.domain.type.CouponPolicyType;
import kr.hhplus.be.server.coupon.infrastructure.entity.CouponPolicyJpaEntity;
import org.springframework.stereotype.Component;

/**
 * CouponPolicyJpaEntity ↔ CouponPolicy 변환 매퍼
 */
@Component
public class CouponPolicyMapper {

    public CouponPolicy toDomain(CouponPolicyJpaEntity entity) {
        return new CouponPolicy(
                entity.getPolicyId(),
                entity.getDiscountRate(),
                entity.getUsagePeriod(),
                CouponPolicyType.valueOf(entity.getType()),
                CouponPolicyStatus.valueOf(entity.getStatus())
        );
    }

    public CouponPolicyJpaEntity toEntity(CouponPolicy domain) {
        return new CouponPolicyJpaEntity(
                domain.getPolicyId(),
                domain.getDiscountRate(),
                domain.getUsagePeriod(),
                domain.getType().name(),
                domain.getStatus().name()
        );
    }
}
