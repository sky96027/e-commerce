package kr.hhplus.be.server.order.infrastructure.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.product.infrastructure.entity.ProductJpaEntity;
import lombok.Getter;

/**
 * 주문 상품 정보를 나타내는 JPA 엔티티 클래스
 */
@Getter
@Entity
@Table(name = "order_item")
public class OrderItemJpaEntity {

    protected OrderItemJpaEntity() {}

    public OrderItemJpaEntity(
            Long orderItemId,
            Long orderId,
            Long productId,
            Long optionId,
            String productName,
            Long productPrice,
            Long discountAmount,
            Long userCouponId,
            Integer quantity
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long orderItemId;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "option_id", nullable = false)
    private Long optionId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_price", nullable = false)
    private Long productPrice;

    @Column(name = "discount_amount", nullable = false)
    private Long discountAmount;

    @Column(name = "user_coupon_id")
    private Long userCouponId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private OrderJpaEntity order;
}