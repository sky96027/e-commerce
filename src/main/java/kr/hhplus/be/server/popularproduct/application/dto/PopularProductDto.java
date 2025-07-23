package kr.hhplus.be.server.popularproduct.application.dto;

import kr.hhplus.be.server.popularproduct.domain.model.PopularProduct;
import kr.hhplus.be.server.product.application.dto.ProductDto;
import kr.hhplus.be.server.product.domain.model.Product;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 인기 상품 정보를 담는 응답 DTO 클래스
 * Application Layer에서 사용되며,
 * 도메인 객체를 외부에 직접 노출하지 않도록 분리한 계층용 DTO
 */
public record PopularProductDto (
        long id,
        long productId,
        int totalSoldQuantity,
        int rank,
        LocalDate referenceDate,
        LocalDateTime createdAt
){
    /**
     * 도메인 모델로부터 DTO로 변환하는 정적 팩토리 메서드
     * @param popularProduct 객체
     * @return PopularProductDto 객체
     */
    public static PopularProductDto from(PopularProduct popularProduct) {
        return new PopularProductDto(
                popularProduct.getId(),
                popularProduct.getProductId(),
                popularProduct.getTotalSoldQuantity(),
                popularProduct.getRank(),
                popularProduct.getReferenceDate(),
                popularProduct.getCreatedAt()
        );
    }
}
