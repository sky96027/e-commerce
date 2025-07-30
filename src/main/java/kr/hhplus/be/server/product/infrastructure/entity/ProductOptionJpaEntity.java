package kr.hhplus.be.server.product.infrastructure.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.product.domain.type.ProductOptionStatus;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 상품 옵션 정보를 나타내는 JPA 엔티티 클래스
 */
@Getter
@Entity
@Table(name = "PRODUCT_OPTION")
public class ProductOptionJpaEntity {
    public ProductOptionJpaEntity() {}

    public ProductOptionJpaEntity(
            long optionId,
            long productId,
            String content,
            ProductOptionStatus status,
            long price,
            int stock,
            LocalDateTime createdAt,
            LocalDateTime expiredAt
    ) {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private long optionId;

    @Column(name = "product_id")
    private long productId;

    @Column(name = "content")
    private String content;

    @Column(name = "status")
    private ProductOptionStatus status;

    @Column(name = "price")
    private long price;

    @Column(name = "stock")
    private int stock;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;
}

