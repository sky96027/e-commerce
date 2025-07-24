package kr.hhplus.be.server.payment.domain.model;

import kr.hhplus.be.server.payment.domain.type.PaymentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentTest {
    @Test
    @DisplayName("Payment 생성자 및 필드 값 정상 할당")
    void constructor_success() {
        // given
        long paymentId = 1L;
        long orderId = 2L;
        long userId = 3L;
        long totalAmount = 10000L;
        long totalDiscountAmount = 2000L;
        PaymentStatus status = PaymentStatus.BEFORE_PAYMENT;

        // when
        Payment payment = new Payment(paymentId, orderId, userId, totalAmount, totalDiscountAmount, status);

        // then
        assertThat(payment.getPaymentId()).isEqualTo(paymentId);
        assertThat(payment.getOrderId()).isEqualTo(orderId);
        assertThat(payment.getUserId()).isEqualTo(userId);
        assertThat(payment.getTotalAmountSnapshot()).isEqualTo(totalAmount);
        assertThat(payment.getTotalDiscountAmountSnapshot()).isEqualTo(totalDiscountAmount);
        assertThat(payment.getStatus()).isEqualTo(status);
    }
} 