package kr.hhplus.be.server.coupon.infrastructure.mapper;

import kr.hhplus.be.server.coupon.domain.model.CouponIssue;
import kr.hhplus.be.server.coupon.domain.type.CouponIssueStatus;
import kr.hhplus.be.server.coupon.domain.type.CouponPolicyType;
import kr.hhplus.be.server.coupon.infrastructure.entity.CouponIssueJpaEntity;
import org.springframework.stereotype.Component;

/**
 * CouponIssueJpaEntity ↔ CouponIssue 변환 매퍼
 */
@Component
public class CouponIssueMapper {

    public CouponIssue toDomain(CouponIssueJpaEntity entity) {
        return new CouponIssue(
                entity.getCouponIssueId(),
                entity.getPolicyId(),
                entity.getTotalIssued(),
                entity.getRemaining(),
                entity.getIssueStartDate(),
                CouponIssueStatus.valueOf(entity.getStatus()),
                entity.getDiscountRateSnapshot(),
                entity.getUsagePeriodSnapshot(),
                CouponPolicyType.valueOf(entity.getTypeSnapshot())
        );
    }

    public CouponIssueJpaEntity toEntity(CouponIssue domain) {
        return new CouponIssueJpaEntity(
                domain.getCouponIssueId(),
                domain.getPolicyId(),
                domain.getTotalIssued(),
                domain.getRemaining(),
                domain.getIssueStartDate(),
                domain.getStatus().name(),
                domain.getDiscountRateSnapshot(),
                domain.getUsagePeriodSnapshot(),
                domain.getTypeSnapshot().name()
        );
    }
}
