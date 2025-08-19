package kr.hhplus.be.server.product.domain.repository;

import kr.hhplus.be.server.product.domain.model.ProductOption;

import java.util.List;

/**
 * 상품 옵션 정보를 조회, 저장하기 위한 도메인 저장소 인터페이스
 * 구현체는 인프라 계층에 위치함
 */
public interface ProductOptionRepository {
    List<ProductOption> findOptionsByProductId(long productId);

    ProductOption findOptionByOptionId(long optionId);

    ProductOption insertOrUpdate(ProductOption productOption);

    void  decrementStock(long optionId, int quantity);

    void  incrementStock(long optionId, int quantity);

    // 비관적 Lock (Legacy)
    /*ProductOption findOptionByOptionIdForUpdate(long optionId);*/
}
