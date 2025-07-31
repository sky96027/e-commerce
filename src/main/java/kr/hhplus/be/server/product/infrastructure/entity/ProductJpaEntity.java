package kr.hhplus.be.server.product.infrastructure.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.product.domain.type.ProductStatus;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 상품 정보를 나타내는 JPA 엔티티 클래스
 */
@Getter
@Entity
@Table(name = "PRODUCT")
public class ProductJpaEntity {
    public ProductJpaEntity() {}

    public ProductJpaEntity(
            Long productId,
            String productName,
            ProductStatus status,
            LocalDateTime createdAt,
            LocalDateTime expiredAt
    ) {
        this.productId = productId;
        this.productName = productName;
        this.status = status;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProductStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;
}

