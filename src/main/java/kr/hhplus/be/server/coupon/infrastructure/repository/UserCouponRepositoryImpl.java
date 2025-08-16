package kr.hhplus.be.server.coupon.infrastructure.repository;

import kr.hhplus.be.server.common.exception.RestApiException;
import kr.hhplus.be.server.coupon.domain.model.UserCoupon;
import kr.hhplus.be.server.coupon.domain.repository.UserCouponRepository;
import kr.hhplus.be.server.coupon.domain.type.UserCouponStatus;
import kr.hhplus.be.server.coupon.exception.CouponErrorCode;
import kr.hhplus.be.server.coupon.infrastructure.entity.UserCouponJpaEntity;
import kr.hhplus.be.server.coupon.infrastructure.mapper.UserCouponMapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * In-memory UserCouponRepository 구현체
 */
@Repository
public class UserCouponRepositoryImpl implements UserCouponRepository {

    private final UserCouponJpaRepository jpaRepository;
    private final UserCouponMapper mapper;

    public UserCouponRepositoryImpl(UserCouponJpaRepository jpaRepository, UserCouponMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    /**
     * 특정 유저의 보유 쿠폰 목록 조회
     */
    @Override
    public List<UserCoupon> findByUserId(long userId) {
        return jpaRepository.findAllByUserId(userId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * userCouponId로 단일 쿠폰 조회
     */
    @Override
    public UserCoupon findByUserCouponId(long userCouponId) {
        return jpaRepository.findById(userCouponId)
                .map(mapper::toDomain)
                .orElseThrow(() -> new RestApiException(CouponErrorCode.USER_COUPON_NOT_FOUND_ERROR));
    }

    /**
     * 유저의 쿠폰을 저장
     */
    @Override
    public UserCoupon insertOrUpdate(UserCoupon userCoupon) {
        UserCouponJpaEntity entity = mapper.toEntity(userCoupon);
        UserCouponJpaEntity saved = jpaRepository.save(entity);
        jpaRepository.flush();

        return mapper.toDomain(saved);
    }
}