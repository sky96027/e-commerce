package kr.hhplus.be.server.payment.application.dto;

import kr.hhplus.be.server.payment.domain.type.PaymentStatus;

/**
 * 결제 저장 요청용 Command DTO
 */
public record SavePaymentCommand(
        long orderId,
        long userId,
        long totalAmount,
        long totalDiscountAmount,
        PaymentStatus status
) {}
