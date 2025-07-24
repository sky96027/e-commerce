package kr.hhplus.be.server.product.application.service;

import kr.hhplus.be.server.product.application.dto.ProductDto;
import kr.hhplus.be.server.product.application.usecase.FindDetailUseCase;
import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;

/**
 * [UseCase 구현체]
 * FindDetailUseCase 인터페이스를 구현한 클래스.
 *
 * 도메인 계층의 ProductRepository를 사용하여 상품 데이터를 조회하고,
 * 그 결과를 ProductDto로 변환하여 반환한다.
 *
 * 이 클래스는 오직 "상품 상세 조회"라는 하나의 유스케이스만 책임지며,
 * 단일 책임 원칙(SRP)을 따르는 구조로 확장성과 테스트 용이성을 높인다.
 */
@Service
public class FindDetailService implements FindDetailUseCase {

    private final ProductRepository productRepository;

    public FindDetailService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * 상품 ID를 기반으로 상세 정보를 조회하고 DTO로 변환한다.
     * @param productId 상품 ID
     * @return 상품 정보 DTO
     */
    @Override
    public ProductDto findById(long productId) {
        Product product = productRepository.selectById(productId);
        if (product == null) {
            throw new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + productId);
        }
        return ProductDto.from(product);  // domain → dto
    }
}