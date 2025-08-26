package kr.hhplus.be.server.payment.application.event;

public record PaymentCompletedEvent(
        long userId,
        long paymentId,
        long totalAmount
) {}
