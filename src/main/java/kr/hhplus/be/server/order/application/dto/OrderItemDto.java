package kr.hhplus.be.server.order.application.dto;

import kr.hhplus.be.server.order.domain.model.OrderItem;

/**
 * 주문 아이템 정보를 표현하는 응답 DTO 클래스
 * Application Layer에서 사용되며,
 * 도메인 객체를 외부에 직접 노출하지 않도록 분리한 계층용 DTO
 */
public record OrderItemDto(
        long orderItemId,
        long orderId,
        long productId,
        long optionId,
        String productName,
        long productPrice,
        long discountAmount,
        Long userCouponId,
        int quantity
) {
    public static OrderItemDto from(OrderItem item) {
        return new OrderItemDto(
                item.getOrderItemId(),
                item.getOrderId(),
                item.getProductId(),
                item.getOptionId(),
                item.getProductName(),
                item.getProductPrice(),
                item.getDiscountAmount(),
                item.getUserCouponId(),
                item.getQuantity()
        );
    }
}