package kr.hhplus.be.server.popularproduct.domain.model;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 인기 상품 도메인 모델
 */
@Getter
public class PopularProduct {
    private final long id;
    private final long productId;
    private final int totalSoldQuantity;
    private final int rank;
    private final LocalDate referenceDate;
    private final LocalDateTime createdAt;

    /**
     * 전체 필드를 초기화하는 생성자
     */
    public PopularProduct(
            long id,
            long productId,
            int totalSoldQuantity,
            int rank,
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
}