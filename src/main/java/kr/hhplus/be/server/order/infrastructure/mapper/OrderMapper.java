package kr.hhplus.be.server.order.infrastructure.mapper;

import kr.hhplus.be.server.order.domain.model.Order;
import kr.hhplus.be.server.order.infrastructure.entity.OrderJpaEntity;
import org.springframework.stereotype.Component;

/**
 * OrderJpaEntity ↔ Order 변환 매퍼
 */
@Component
public class OrderMapper {

    public Order toDomain(OrderJpaEntity entity) {
        return new Order(
                entity.getOrderId(),
                entity.getUserId(),
                entity.getTotalAmount(),
                entity.getTotalDiscountAmount(),
                entity.getStatus(),
                entity.getOrderAt()
        );
    }

    public OrderJpaEntity toEntity(Order domain) {
        return new OrderJpaEntity(
                domain.getOrderId(),
                domain.getUserId(),
                domain.getTotalAmount(),
                domain.getTotalDiscountAmount(),
                domain.getStatus(),
                domain.getOrderAt()
        );
    }
}
