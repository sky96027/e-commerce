package kr.hhplus.be.server.order.domain.model;

import lombok.Getter;

/**
 * 주문 항목 도메인 모델
 */
@Getter
public class OrderItem {

    private final Long orderItemId;
    private final long orderId;
    private final long productId;
    private final long optionId;
    private final String productName;
    private final long productPrice;
    private final long discountAmount;
    private final Long userCouponId;
    private final int quantity;

    /**
     * 전체 필드를 초기화하는 생성자
     */
    public OrderItem(
            Long orderItemId,
            long orderId,
            long productId,
            long optionId,
            String productName,
            long productPrice,
            long discountAmount,
            Long userCouponId,
            int quantity
    ) {
        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.productId = productId;
        this.optionId = optionId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.discountAmount = discountAmount;
        this.userCouponId = userCouponId;
        this.quantity = quantity;
    }
}
