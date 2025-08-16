package kr.hhplus.be.server.coupon.infrastructure.repository;

import kr.hhplus.be.server.common.exception.RestApiException;
import kr.hhplus.be.server.coupon.domain.model.CouponPolicy;
import kr.hhplus.be.server.coupon.domain.repository.CouponPolicyRepository;
import kr.hhplus.be.server.coupon.exception.CouponErrorCode;
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
                .orElseThrow(() -> new RestApiException(CouponErrorCode.COUPON_POLICY_NOT_FOUND_ERROR));
    }

    @Override
    public CouponPolicy update(CouponPolicy couponPolicy) {
        CouponPolicyJpaEntity entity = mapper.toEntity(couponPolicy);
        CouponPolicyJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}
