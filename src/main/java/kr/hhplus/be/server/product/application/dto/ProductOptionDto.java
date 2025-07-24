package kr.hhplus.be.server.product.application.dto;

import kr.hhplus.be.server.product.domain.model.ProductOption;
import kr.hhplus.be.server.product.domain.type.ProductOptionStatus;

import java.time.LocalDateTime;

/**
 * 상품 옵션 정보를 담는 응답 DTO 클래스
 * Application Layer에서 사용되며,
 * 도메인 객체(ProductOption)를 외부에 직접 노출하지 않도록 분리한 계층용 DTO
 */
public record ProductOptionDto(
        long optionId,
        long productId,
        String content,
        ProductOptionStatus status,
        long price,
        int stock,
        LocalDateTime createdAt,
        LocalDateTime expiredAt
) {
    public static ProductOptionDto from(ProductOption option) {
        return new ProductOptionDto(
                option.getOptionId(),
                option.getProductId(),
                option.getContent(),
                option.getStatus(),
                option.getPrice(),
                option.getStock(),
                option.getCreatedAt(),
                option.getExpiredAt()
        );
    }
}