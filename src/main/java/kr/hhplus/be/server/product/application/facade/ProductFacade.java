package kr.hhplus.be.server.product.application.facade;

import kr.hhplus.be.server.product.application.dto.ProductDetailDto;
import kr.hhplus.be.server.product.application.dto.ProductDto;
import kr.hhplus.be.server.product.application.dto.ProductOptionDto;
import kr.hhplus.be.server.product.application.dto.ProductSummaryDto;
import kr.hhplus.be.server.product.application.usecase.FindDetailUseCase;
import kr.hhplus.be.server.product.application.usecase.FindProductOptionsUseCase;
import kr.hhplus.be.server.product.application.usecase.FindSummariesUseCase;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * [Facade]
 * 상품 도메인과 옵션 도메인을 조합하여 외부에 통합된 정보를 제공하는 파사드 클래스.
 * 상품 목록/상세 조회 시 도메인 간 경계를 넘지 않도록 유스케이스를 조합한다.
 */
@Component
public class ProductFacade {

    private final FindSummariesUseCase findSummariesUseCase;
    private final FindDetailUseCase findDetailUseCase;
    private final FindProductOptionsUseCase findProductOptionsUseCase;

    public ProductFacade(
            FindSummariesUseCase findSummariesUseCase,
            FindDetailUseCase findDetailUseCase,
            FindProductOptionsUseCase findProductOptionsUseCase
    ) {
        this.findSummariesUseCase = findSummariesUseCase;
        this.findDetailUseCase = findDetailUseCase;
        this.findProductOptionsUseCase = findProductOptionsUseCase;
    }

    /**
     * 전체 상품 목록을 조회하고, 각 상품의 옵션 중 최저가를 함께 반환한다.
     * 상품과 옵션은 각각의 유스케이스를 통해 도메인 경계를 유지한 채 조회된다.
     *
     * @return 상품 요약 정보 리스트 (상품명, 최저 옵션 가격 등 포함)
     */
    public List<ProductSummaryDto> getProductSummaries() {
        return findSummariesUseCase.findSummaries(0L).stream()
                .map(product -> {
                    List<ProductOptionDto> options = findProductOptionsUseCase.findByProductId(product.productId());
                    long minPrice = options.stream()
                            .mapToLong(ProductOptionDto::price)
                            .min()
                            .orElse(0L);

                    return ProductSummaryDto.from(product, minPrice);
                })
                .collect(Collectors.toList());
    }

    /**
     * 단일 상품의 상세 정보를 조회하고, 해당 상품의 옵션 목록도 함께 반환한다.
     * 두 도메인의 유스케이스를 조합하여 상세 응답을 구성한다.
     *
     * @param productId 조회할 상품 ID
     * @return 상품 정보 + 옵션 목록을 포함한 상세 DTO
     */
    public ProductDetailDto getProductDetail(long productId) {
        ProductDto product = findDetailUseCase.findById(productId);
        List<ProductOptionDto> options = findProductOptionsUseCase.findByProductId(productId);

        return ProductDetailDto.from(product, options);
    }
}