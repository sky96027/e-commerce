package kr.hhplus.be.server.order.application.dto;

/**
 * 주문 아이템 저장 요청용 Command DTO
 */
public record SaveOrderItemCommand(
        long productId,
        long optionId,
        String productName,
        long productPrice,
        Long userCouponId,  // optional
        int quantity
) {}
