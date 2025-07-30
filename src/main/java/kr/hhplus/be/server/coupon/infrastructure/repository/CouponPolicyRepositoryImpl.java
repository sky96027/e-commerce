package kr.hhplus.be.server.coupon.infrastructure.repository;

import kr.hhplus.be.server.coupon.domain.model.CouponPolicy;
import kr.hhplus.be.server.coupon.domain.repository.CouponPolicyRepository;
import kr.hhplus.be.server.coupon.infrastructure.entity.CouponPolicyJpaEntity;
import kr.hhplus.be.server.coupon.infrastructure.mapper.CouponPolicyMapper;
import org.springframework.stereotype.Repository;

/**
 * In-memory UserCouponRepository 구현체
 */
@Repository
public class CouponPolicyRepositoryImpl implements CouponPolicyRepository {

    private final CouponPolicyJpaRepository jpaRepository;
    private final CouponPolicyMapper mapper;

    public CouponPolicyRepositoryImpl(CouponPolicyJpaRepository jpaRepository, CouponPolicyMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public CouponPolicy findById(long couponPolicyId) {
        return jpaRepository.findById(couponPolicyId)
                .map(mapper::toDomain)
                .orElseThrow(() -> new IllegalArgumentException("해당 쿠폰 정책이 존재하지 않습니다: " + couponPolicyId));
    }

    @Override
    public void update(CouponPolicy couponPolicy) {
        CouponPolicyJpaEntity entity = mapper.toEntity(couponPolicy);
        jpaRepository.save(entity);
    }
}
