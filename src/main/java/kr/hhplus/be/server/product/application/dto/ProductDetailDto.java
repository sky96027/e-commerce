package kr.hhplus.be.server.product.application.dto;

import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.domain.type.ProductStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 상품 상세 정보를 담는 DTO
 * - 상세 조회 시 사용
 */
public record ProductDetailDto(
        long productId,
        String productName,
        ProductStatus status,
        LocalDateTime createdAt,
        LocalDateTime expiredAt,
        List<ProductOptionDto> options
) {
    public static ProductDetailDto from(Product product, List<ProductOptionDto> options) {
        return new ProductDetailDto(
                product.getProductId(),
                product.getProductName(),
                product.getStatus(),
                product.getCreatedAt(),
                product.getExpiredAt(),
                options
        );
    }

    // DTO 기반 생성 (UseCase 계층에서 활용)
    public static ProductDetailDto from(ProductDto product, List<ProductOptionDto> options) {
        return new ProductDetailDto(
                product.productId(),
                product.productName(),
                product.status(),
                product.createdAt(),
                product.expiredAt(),
                options
        );
    }
}