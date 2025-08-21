package kr.hhplus.be.server.coupon.infrastructure.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.coupon.infrastructure.entity.CouponIssueJpaEntity;
import kr.hhplus.be.server.user.infrastructure.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
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
    @Query("select c.remaining from CouponIssueJpaEntity c where c.couponIssueId = :id")
    Optional<Integer> findRemainingById(@Param("id") long couponIssueId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update CouponIssueJpaEntity c
           set c.remaining = c.remaining - 1
         where c.couponIssueId = :id
           and c.remaining > 0
    """)
    int decrementRemaining(@Param("id") long couponIssueId);
}
