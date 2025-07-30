package kr.hhplus.be.server.popularproduct.application.service;

import kr.hhplus.be.server.popularproduct.application.dto.PopularProductDto;
import kr.hhplus.be.server.popularproduct.domain.model.PopularProduct;
import kr.hhplus.be.server.popularproduct.domain.repository.PopularProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class FindPopularProductSummaryServiceTest {
    @Mock
    private PopularProductRepository popularProductRepository;
    @InjectMocks
    private FindPopularProductSummaryService findPopularProductSummaryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        findPopularProductSummaryService = new FindPopularProductSummaryService(popularProductRepository);
    }

    @Test
    @DisplayName("인기 상품 목록 정상 반환")
    void findSummary_success() {
        // given
        PopularProduct product = new PopularProduct(1L, 10L, 100, 1, LocalDate.now(), LocalDateTime.now());
        when(popularProductRepository.findAllSummaries()).thenReturn(List.of(product));

        // when
        List<PopularProductDto> result = findPopularProductSummaryService.findSummary();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).productId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("인기 상품이 없을 때 빈 목록 반환")
    void findSummary_empty() {
        // given
        when(popularProductRepository.findAllSummaries()).thenReturn(Collections.emptyList());

        // when
        List<PopularProductDto> result = findPopularProductSummaryService.findSummary();

        // then
        assertThat(result).isEmpty();
    }
} 