package kr.hhplus.be.server.product.application.dto;

import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.domain.type.ProductStatus;

import java.time.LocalDateTime;

/**
 * 상품 정보를 담는 응답 DTO 클래스
 * Application Layer에서 사용되며,
 * 도메인 객체(Product)를 외부에 직접 노출하지 않도록 분리한 계층용 DTO
 */
public record ProductDto(
        long productId,            // 상품 ID
        String productName,        // 상품 이름
        ProductStatus status,      // 상품 판매 상태 (판매 중, 품절 등)
        LocalDateTime createdAt,   // 상품 등록일
        LocalDateTime expiredAt    // 상품 만료일 (null일 수 있음)
) {
    /**
     * 도메인 모델(Product)로부터 DTO로 변환하는 정적 팩토리 메서드
     * @param product 객체
     * @return ProductDto 객체
     */
    public static ProductDto from(Product product) {
        return new ProductDto(
                product.getProductId(),
                product.getProductName(),
                product.getStatus(),
                product.getCreatedAt(),
                product.getExpiredAt()
        );
    }
}