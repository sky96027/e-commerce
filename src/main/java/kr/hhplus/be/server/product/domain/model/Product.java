package kr.hhplus.be.server.product.domain.model;

import kr.hhplus.be.server.product.domain.type.ProductStatus;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 상품 도메인 모델
 */
@Getter
public class Product {

    private final Long productId;
    private final String productName;
    private final ProductStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime expiredAt;

    /**
     * 전체 필드를 초기화하는 생성자
     */
    public Product(
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
}