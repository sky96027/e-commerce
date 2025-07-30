package kr.hhplus.be.server.coupon.infrastructure.mapper;

import kr.hhplus.be.server.coupon.domain.model.UserCoupon;
import kr.hhplus.be.server.coupon.domain.type.CouponPolicyType;
import kr.hhplus.be.server.coupon.domain.type.UserCouponStatus;
import kr.hhplus.be.server.coupon.infrastructure.entity.UserCouponJpaEntity;
import org.springframework.stereotype.Component;

/**
 * UserCouponJpaEntity ↔ UserCoupon 변환 매퍼
 */
@Component
public class UserCouponMapper {

    public UserCoupon toDomain(UserCouponJpaEntity entity) {
        return new UserCoupon(
                entity.getUserCouponId(),
                entity.getCouponId(),
                entity.getUserId(),
                entity.getPolicyId(),
                UserCouponStatus.valueOf(entity.getStatus()),
                CouponPolicyType.valueOf(entity.getTypeSnapshot()),
                entity.getDiscountRateSnapshot(),
                entity.getUsagePeriodSnapshot(),
                entity.getExpiredAt()
        );
    }

    public UserCouponJpaEntity toEntity(UserCoupon domain) {
        return new UserCouponJpaEntity(
                domain.getUserCouponId(),
                domain.getCouponId(),
                domain.getUserId(),
                domain.getPolicyId(),
                domain.getStatus().name(),
                domain.getTypeSnapshot().name(),
                domain.getDiscountRateSnapshot(),
                domain.getUsagePeriodSnapshot(),
                domain.getExpiredAt()
        );
    }
}
