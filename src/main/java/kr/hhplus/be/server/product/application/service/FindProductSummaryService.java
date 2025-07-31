package kr.hhplus.be.server.product.application.service;

import kr.hhplus.be.server.product.application.dto.ProductDto;
import kr.hhplus.be.server.product.application.usecase.FindProductSummaryUseCase;
import kr.hhplus.be.server.product.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * [UseCase 구현체]
 * FindSummaryUseCase 인터페이스를 구현한 클래스.
 *
 * 도메인 계층의 ProductRepository를 사용하여 상품 데이터를 조회하고,
 * 그 결과를 ProductDto List로 변환하여 반환한다.
 *
 * 이 클래스는 오직 "상품 목록 조회"라는 하나의 유스케이스만 책임지며,
 * 단일 책임 원칙(SRP)을 따르는 구조로 확장성과 테스트 용이성을 높인다.
 */
@Service
public class FindProductSummaryService implements FindProductSummaryUseCase {

    private final ProductRepository productRepository;

    public FindProductSummaryService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * 전체 상품 목록을 조회하여 DTO 리스트로 변환한다.
     * @return 상품 DTO 목록
     */
    @Override
    public List<ProductDto> findSummary() {
        return productRepository.findAllSummaries().stream()
                .map(ProductDto::from)  // domain → dto
                .collect(Collectors.toList());
    }
}