package kr.hhplus.be.server.coupon.infrastructure.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.coupon.infrastructure.entity.CouponIssueJpaEntity;
import kr.hhplus.be.server.user.infrastructure.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponIssueJpaRepository extends JpaRepository<CouponIssueJpaEntity, Long> {
    // 비관적 락 (Legacy)
    /*@Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CouponIssueJpaEntity c WHERE c.couponIssueId = :id")
    Optional<CouponIssueJpaEntity> findByIdForUpdate(@Param("id") Long id);*/
}
