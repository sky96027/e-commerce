package kr.hhplus.be.server.product.application.service;

import kr.hhplus.be.server.product.application.dto.ProductDto;
import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.domain.repository.ProductRepository;
import kr.hhplus.be.server.product.domain.type.ProductStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class FindProductSummaryServiceTest {
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private FindProductSummaryService findProductSummaryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        findProductSummaryService = new FindProductSummaryService(productRepository);
    }

    @Test
    @DisplayName("상품 목록 정상 반환")
    void findSummary_success() {
        // given
        Product product = new Product(1L, "테스트상품", ProductStatus.ON_SALE, LocalDateTime.now(), null);
        when(productRepository.findAllSummaries()).thenReturn(List.of(product));

        // when
        List<ProductDto> result = findProductSummaryService.findSummary();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).productId()).isEqualTo(1L);
        assertThat(result.get(0).productName()).isEqualTo("테스트상품");
    }

    @Test
    @DisplayName("상품이 없을 때 빈 목록 반환")
    void findSummary_empty() {
        // given
        when(productRepository.findAllSummaries()).thenReturn(Collections.emptyList());

        // when
        List<ProductDto> result = findProductSummaryService.findSummary();

        // then
        assertThat(result).isEmpty();
    }
} 