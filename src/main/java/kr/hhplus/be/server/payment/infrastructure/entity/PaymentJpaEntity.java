package kr.hhplus.be.server.payment.infrastructure.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.payment.domain.type.PaymentStatus;
import lombok.Getter;

/**
 * 결제 정보를 나타내는 JPA 엔티티 클래스
 */
@Getter
@Entity
@Table(name = "PAYMENT")
public class PaymentJpaEntity {
    public PaymentJpaEntity() {}

    public PaymentJpaEntity(
            Long paymentId,
            Long orderId,
            Long userId,
            Long totalAmountSnapshot,
            Long totalDiscountAmountSnapshot,
            PaymentStatus status
    ) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.userId = userId;
        this.totalAmountSnapshot = totalAmountSnapshot;
        this.totalDiscountAmountSnapshot = totalDiscountAmountSnapshot;
        this.status = status;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "total_amount_snapshot", nullable = false)
    private Long totalAmountSnapshot;

    @Column(name = "total_discount_amount_snapshot", nullable = false)
    private Long totalDiscountAmountSnapshot;

    @Column(name = "status", nullable = false, length = 50)
    private PaymentStatus status;
}