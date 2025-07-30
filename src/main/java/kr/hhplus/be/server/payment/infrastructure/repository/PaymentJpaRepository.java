package kr.hhplus.be.server.payment.infrastructure.repository;

import kr.hhplus.be.server.payment.infrastructure.entity.PaymentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentJpaRepository extends JpaRepository<PaymentJpaEntity, Long> {
}
