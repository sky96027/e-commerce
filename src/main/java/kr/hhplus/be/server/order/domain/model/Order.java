package kr.hhplus.be.server.order.domain.model;

import kr.hhplus.be.server.order.domain.type.OrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 주문 도메인 모델
 */
@Getter
public class Order {
    private final long orderId;
    private final long userId;
    private final long totalAmount;
    private final long totalDiscountAmount;
    private final OrderStatus status;
    private final LocalDateTime orderAt;

    /**
     * 전체 필드를 초기화하는 생성자
     */

    public Order(
            long orderId,
            long userId,
            long totalAmount,
            long totalDiscountAmount,
            OrderStatus status,
            LocalDateTime orderAt
    ) {
        this.orderId = orderId;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.totalDiscountAmount = totalDiscountAmount;
        this.status = status;
        this.orderAt = orderAt;
    }
}
