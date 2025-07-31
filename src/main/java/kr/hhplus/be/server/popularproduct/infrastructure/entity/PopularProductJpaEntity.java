package kr.hhplus.be.server.popularproduct.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 상품 정보를 나타내는 JPA 엔티티 클래스
 */
@Getter
@Entity
@Table(name = "POPULAR_PRODUCT")
public class PopularProductJpaEntity {
    public PopularProductJpaEntity() {}

    public PopularProductJpaEntity(
            Long id,
            long productId,
            Integer totalSoldQuantity,
            Integer rank,
            LocalDate referenceDate,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.productId = productId;
        this.totalSoldQuantity = totalSoldQuantity;
        this.rank = rank;
        this.referenceDate = referenceDate;
        this.createdAt = createdAt;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "total_sold_quantity", nullable = false)
    private Integer totalSoldQuantity;

    @Column(name = "`rank`", nullable = false)
    private Integer rank;

    @Column(name = "reference_date", nullable = false)
    private LocalDate referenceDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
