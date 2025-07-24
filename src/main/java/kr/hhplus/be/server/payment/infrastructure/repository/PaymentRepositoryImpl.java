package kr.hhplus.be.server.payment.infrastructure.repository;

import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.domain.repository.PaymentRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * In-memory OrderRepository 구현체
 */
@Repository
public class PaymentRepositoryImpl implements PaymentRepository {
    private final Map<Long, Payment> table = new HashMap<>(); // key = orderId

    @Override
    public void save(Payment payment) {
        table.put(payment.getOrderId(), payment);
    }

    @Override
    public Payment findByOrderId(long orderId) {
        return table.get(orderId);
    }
}
