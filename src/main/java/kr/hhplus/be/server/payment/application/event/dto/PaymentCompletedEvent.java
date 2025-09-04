package kr.hhplus.be.server.payment.application.event.dto;

public record PaymentCompletedEvent(
        long userId,
        long paymentId,
        long totalAmount
) {}
