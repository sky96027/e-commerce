package kr.hhplus.be.server.product.application.dto;

import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.domain.type.ProductStatus;

import java.time.LocalDateTime;

/**
 * 상품 요약 정보를 담는 DTO
 * - 목록 조회 시 사용
 */
public record ProductSummaryDto(
        long productId,
        String productName,
        ProductStatus status,
        long minPrice,
        LocalDateTime createdAt,
        LocalDateTime expiredAt
) {
    public static ProductSummaryDto from(Product product, long minPrice) {
        return new ProductSummaryDto(
                product.getProductId(),
                product.getProductName(),
                product.getStatus(),
                minPrice,
                product.getCreatedAt(),
                product.getExpiredAt()
        );
    }

    public static ProductSummaryDto from(ProductDto product, long minPrice) {
        return new ProductSummaryDto(
                product.productId(),
                product.productName(),
                product.status(),
                minPrice,
                product.createdAt(),
                product.expiredAt()
        );
    }
}