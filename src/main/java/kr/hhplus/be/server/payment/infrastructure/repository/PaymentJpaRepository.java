package kr.hhplus.be.server.payment.infrastructure.repository;

import kr.hhplus.be.server.payment.infrastructure.entity.PaymentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentJpaRepository extends JpaRepository<PaymentJpaEntity, Long> {
    Optional<PaymentJpaEntity> findByOrderId(Long orderId);
}
