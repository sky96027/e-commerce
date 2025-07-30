package kr.hhplus.be.server.payment.infrastructure.repository;

import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.domain.repository.PaymentRepository;
import kr.hhplus.be.server.payment.infrastructure.entity.PaymentJpaEntity;
import kr.hhplus.be.server.payment.infrastructure.mapper.PaymentMapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * In-memory OrderRepository 구현체
 */
@Repository
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository jpaRepository;
    private final PaymentMapper mapper;

    public PaymentRepositoryImpl(PaymentJpaRepository jpaRepository, PaymentMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public void save(Payment payment) {
        PaymentJpaEntity entity = mapper.toEntity(payment);
        jpaRepository.save(entity);
    }

    @Override
    public Payment findById(long orderId) {
        return jpaRepository.findById(orderId)
                .map(mapper::toDomain)
                .orElse(null);
    }
}
