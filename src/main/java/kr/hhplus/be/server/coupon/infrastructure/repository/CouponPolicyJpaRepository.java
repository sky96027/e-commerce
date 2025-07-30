package kr.hhplus.be.server.coupon.infrastructure.repository;

import kr.hhplus.be.server.coupon.infrastructure.entity.CouponPolicyJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponPolicyJpaRepository extends JpaRepository<CouponPolicyJpaEntity, Long> {
}
