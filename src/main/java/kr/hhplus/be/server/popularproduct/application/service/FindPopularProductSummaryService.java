package kr.hhplus.be.server.popularproduct.application.service;

import kr.hhplus.be.server.popularproduct.application.dto.PopularProductDto;
import kr.hhplus.be.server.popularproduct.application.usecase.FindPopularProductSummaryUseCase;
import kr.hhplus.be.server.popularproduct.domain.repository.PopularProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * [UseCase 구현체]
 * FindSummariesUseCase 인터페이스를 구현한 클래스.
 *
 * 도메인 계층의 ProductRepository를 사용하여 상품 데이터를 조회하고,
 * 그 결과를 ProductDto List로 변환하여 반환한다.
 *
 * 이 클래스는 오직 "상품 목록 조회"라는 하나의 유스케이스만 책임지며,
 * 단일 책임 원칙(SRP)을 따르는 구조로 확장성과 테스트 용이성을 높인다.
 */
@Service
public class FindPopularProductSummaryService implements FindPopularProductSummaryUseCase {

    private final PopularProductRepository popularProductRepository;

    public FindPopularProductSummaryService(PopularProductRepository popularProductRepository) {
        this.popularProductRepository = popularProductRepository;
    }

    /**
     * 전체 상품 목록을 조회하여 DTO 리스트로 변환한다.
     * @return 상품 DTO 목록
     */
    @Override
    public List<PopularProductDto> findSummary() {
        return popularProductRepository.selectSummaries().stream()
                .map(PopularProductDto::from)  // domain → dto
                .collect(Collectors.toList());
    }
}
