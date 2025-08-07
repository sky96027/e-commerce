package kr.hhplus.be.server.coupon.infrastructure.repository;

import kr.hhplus.be.server.coupon.domain.model.CouponIssue;
import kr.hhplus.be.server.coupon.domain.repository.CouponIssueRepository;
import kr.hhplus.be.server.coupon.domain.type.CouponIssueStatus;
import kr.hhplus.be.server.coupon.domain.type.CouponPolicyType;
import kr.hhplus.be.server.coupon.infrastructure.entity.CouponIssueJpaEntity;
import kr.hhplus.be.server.coupon.infrastructure.mapper.CouponIssueMapper;
import kr.hhplus.be.server.user.domain.model.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * In-memory CouponIssueRepository 구현체
 */
@Repository
public class CouponIssueRepositoryImpl implements CouponIssueRepository {

    private final CouponIssueJpaRepository jpaRepository;
    private final CouponIssueMapper mapper;

    public CouponIssueRepositoryImpl(CouponIssueJpaRepository jpaRepository, CouponIssueMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public CouponIssue findById(long couponIssueId) {
        return jpaRepository.findById(couponIssueId)
                .map(mapper::toDomain)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 쿠폰 발급 정보가 존재하지 않습니다. id=" + couponIssueId));
    }

    @Override
    public CouponIssue save(CouponIssue couponIssue) {
        return mapper.toDomain(
                jpaRepository.save(mapper.toEntity(couponIssue))
        );
    }

    @Override
    public CouponIssue findByIdForUpdate(long CouponIssueId) {
        return jpaRepository.findByIdForUpdate(CouponIssueId)
                .map(mapper::toDomain)
                .orElse(null);
    }
}