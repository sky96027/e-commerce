package kr.hhplus.be.server.product.application.service;

import kr.hhplus.be.server.product.application.dto.ProductOptionDto;
import kr.hhplus.be.server.product.application.usecase.FindProductOptionsUseCase;
import kr.hhplus.be.server.product.domain.repository.ProductOptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * [UseCase 구현체]
 * FindProductOptionsUseCase 인터페이스를 구현한 클래스.
 *
 * 도메인 계층의 ProductOptionRepository를 사용하여 특정 상품의 옵션들을 조회하고,
 * 그 결과를 ProductOptionDto List로 변환하여 반환한다.
 *
 * 이 클래스는 오직 "상품 옵션 조회"라는 하나의 유스케이스만 책임지며,
 * 단일 책임 원칙(SRP)을 따르는 구조로 확장성과 테스트 용이성을 높인다.
 */
@Service
public class FindProductOptionsService implements FindProductOptionsUseCase {

    private final ProductOptionRepository productOptionRepository;

    public FindProductOptionsService(ProductOptionRepository productOptionRepository) {
        this.productOptionRepository = productOptionRepository;
    }

    /**
     * 상품 ID를 기반으로 해당 상품의 옵션들을 조회하여 DTO 리스트로 반환한다.
     * @param productId 상품 ID
     * @return 상품 옵션 DTO 목록
     */
    @Override
    public List<ProductOptionDto> findByProductId(long productId) {
        return productOptionRepository.findOptionsByProductId(productId).stream()
                .map(ProductOptionDto::from)  // domain → dto
                .collect(Collectors.toList());
    }
}