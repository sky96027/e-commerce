package kr.hhplus.be.server.coupon.infrastructure.repository;

import kr.hhplus.be.server.coupon.infrastructure.entity.UserCouponJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCouponJpaRepository extends JpaRepository<UserCouponJpaEntity, Long> {
    List<UserCouponJpaEntity> findAllByUserId(Long userId);
}
