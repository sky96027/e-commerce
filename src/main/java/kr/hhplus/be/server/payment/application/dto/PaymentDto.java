package kr.hhplus.be.server.payment.application.dto;

import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.domain.type.PaymentStatus;

/**
 * 결제 정보를 표현하는 응답 DTO 클래스
 * Application Layer에서 사용되며,
 * 도메인 객체를 외부에 직접 노출하지 않도록 분리한 계층용 DTO
 */
public record PaymentDto(
        long paymentId,
        long orderId,
        long userId,
        long totalAmountSnapshot,
        long totalDiscountAmountSnapshot,
        PaymentStatus status
) {
    public static PaymentDto from(Payment payment) {
        return new PaymentDto(
                payment.getPaymentId(),
                payment.getOrderId(),
                payment.getUserId(),
                payment.getTotalAmountSnapshot(),
                payment.getTotalDiscountAmountSnapshot(),
                payment.getStatus()
        );
    }
}
