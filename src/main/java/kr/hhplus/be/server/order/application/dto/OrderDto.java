package kr.hhplus.be.server.order.application.dto;

import kr.hhplus.be.server.order.domain.model.Order;
import kr.hhplus.be.server.order.domain.model.OrderItem;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 주문 정보를 표현하는 응답 DTO 클래스
 * Application Layer에서 사용되며,
 * 도메인 객체를 외부에 직접 노출하지 않도록 분리한 계층용 DTO
 */
public record OrderDto(
        long orderId,
        long userId,
        long totalAmount,
        long totalDiscountAmount,
        String status,
        LocalDateTime orderAt,
        List<OrderItemDto> items
) {
    public static OrderDto from(Order order, List<OrderItem> items) {
        return new OrderDto(
                order.getOrderId(),
                order.getUserId(),
                order.getTotalAmount(),
                order.getTotalDiscountAmount(),
                order.getStatus().name(),
                order.getOrderAt(),
                items.stream().map(OrderItemDto::from).toList()
        );
    }
}
