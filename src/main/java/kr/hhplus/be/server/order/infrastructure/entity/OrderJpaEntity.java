package kr.hhplus.be.server.order.infrastructure.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.order.domain.type.OrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 주문 정보를 나타내는 JPA 엔티티 클래스
 */
@Getter
@Entity
@Table(name = "`order`")
public class OrderJpaEntity {

    protected OrderJpaEntity() {}

    public OrderJpaEntity(
            Long orderId,
            Long userId,
            Long totalAmount,
            Long totalDiscountAmount,
            OrderStatus status,
            LocalDateTime orderedAt
    ) {
        this.orderId = orderId;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.totalDiscountAmount = totalDiscountAmount;
        this.status = status;
        this.orderAt = orderedAt;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "total_amount", nullable = false)
    private Long totalAmount;

    @Column(name = "total_discount_amount", nullable = false)
    private Long totalDiscountAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private OrderStatus status;

    @Column(name = "ordered_at", nullable = false)
    private LocalDateTime orderAt;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderItemJpaEntity> items = new ArrayList<>();
}