package kr.hhplus.be.server.payment.domain.model;

import kr.hhplus.be.server.payment.domain.type.PaymentStatus;
import lombok.Getter;

/**
 * 결제 도메인 모델
 */
@Getter
public class Payment {
    private final long paymentId;
    private final long orderId;
    private final long userId;
    private final long totalAmountSnapshot;
    private final long totalDiscountAmountSnapshot;
    private final PaymentStatus status;

    /**
     * 전체 필드를 초기화하는 생성자
     */

    public Payment(
            long paymentId,
            long orderId,
            long userId,
            long totalAmountSnapshot,
            long totalDiscountAmountSnapshot,
            PaymentStatus status
    ) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.userId = userId;
        this.totalAmountSnapshot = totalAmountSnapshot;
        this.totalDiscountAmountSnapshot = totalDiscountAmountSnapshot;
        this.status = status;
    }
}


