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
@Table(name = "product")
public class ProductJpaEntity {
    public ProductJpaEntity() {}

    public ProductJpaEntity(
            long productId, String productName, ProductStatus status,
            LocalDateTime createdAt, LocalDateTime expiredAt
    ) {}

    @Id
    @Column(name = "product_id")
    private long productId;

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

