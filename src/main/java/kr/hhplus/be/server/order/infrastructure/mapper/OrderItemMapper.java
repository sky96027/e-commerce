package kr.hhplus.be.server.order.infrastructure.mapper;

import kr.hhplus.be.server.order.domain.model.OrderItem;
import kr.hhplus.be.server.order.infrastructure.entity.OrderItemJpaEntity;
import org.springframework.stereotype.Component;

/**
 * OrderItemJpaEntity ↔ OrderItem 변환 매퍼
 */
@Component
public class OrderItemMapper {

    public OrderItem toDomain(OrderItemJpaEntity entity) {
        return new OrderItem(
                entity.getOrderItemId(),
                entity.getOrderId(),
                entity.getProductId(),
                entity.getOptionId(),
                entity.getProductName(),
                entity.getProductPrice(),
                entity.getDiscountAmount(),
                entity.getUserCouponId(),
                entity.getQuantity()
        );
    }

    public OrderItemJpaEntity toEntity(OrderItem domain) {
        return new OrderItemJpaEntity(
                domain.getOrderItemId(),
                domain.getOrderId(),
                domain.getProductId(),
                domain.getOptionId(),
                domain.getProductName(),
                domain.getProductPrice(),
                domain.getDiscountAmount(),
                domain.getUserCouponId(),
                domain.getQuantity()
        );
    }
}
