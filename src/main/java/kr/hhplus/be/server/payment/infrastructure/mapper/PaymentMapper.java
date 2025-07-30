package kr.hhplus.be.server.payment.infrastructure.mapper;

import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.infrastructure.entity.PaymentJpaEntity;
import org.springframework.stereotype.Component;

/**
 * PaymentJpaEntity ↔ Payment 변환 매퍼
 */
@Component
public class PaymentMapper {

    public Payment toDomain(PaymentJpaEntity entity) {
        return new Payment(
                entity.getPaymentId(),
                entity.getOrderId(),
                entity.getUserId(),
                entity.getTotalAmountSnapshot(),
                entity.getTotalDiscountAmountSnapshot(),
                entity.getStatus()
        );
    }

    public PaymentJpaEntity toEntity(Payment domain) {
        return new PaymentJpaEntity(
                domain.getPaymentId(),
                domain.getOrderId(),
                domain.getUserId(),
                domain.getTotalAmountSnapshot(),
                domain.getTotalDiscountAmountSnapshot(),
                domain.getStatus()
        );
    }
}
