package kr.hhplus.be.server.product.application.service;

import kr.hhplus.be.server.product.domain.model.ProductOption;
import kr.hhplus.be.server.product.domain.repository.ProductOptionRepository;
import kr.hhplus.be.server.product.domain.type.ProductOptionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DeductStockServiceTest {
    @Mock
    private ProductOptionRepository productOptionRepository;
    @InjectMocks
    private DeductStockService deductStockService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        deductStockService = new DeductStockService(productOptionRepository);
    }

    @Test
    @DisplayName("정상 재고 차감")
    void deductStock_success() {
        // given
        long optionId = 1L;
        ProductOption option = new ProductOption(optionId, 2L, "옵션", ProductOptionStatus.ON_SALE, 10000L, 10, LocalDateTime.now(), null);
        ProductOption updated = new ProductOption(optionId, 2L, "옵션", ProductOptionStatus.ON_SALE, 10000L, 8, option.getCreatedAt(), null);
        when(productOptionRepository.findByOptionId(optionId)).thenReturn(option);
        doNothing().when(productOptionRepository).insertOrUpdate(any(ProductOption.class));

        // when
        deductStockService.deductStock(optionId, 2);

        // then
        verify(productOptionRepository, times(1)).findByOptionId(optionId);
        verify(productOptionRepository, times(1)).insertOrUpdate(any(ProductOption.class));
    }

    @Test
    @DisplayName("재고 부족 시 예외 발생")
    void deductStock_insufficientStock_throwsException() {
        // given
        long optionId = 2L;
        ProductOption option = new ProductOption(optionId, 2L, "옵션", ProductOptionStatus.ON_SALE, 10000L, 1, LocalDateTime.now(), null);
        when(productOptionRepository.findByOptionId(optionId)).thenReturn(option);

        // when & then
        assertThatThrownBy(() -> deductStockService.deductStock(optionId, 2))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("재고가 부족합니다");
        verify(productOptionRepository, times(1)).findByOptionId(optionId);
        verify(productOptionRepository, never()).insertOrUpdate(any(ProductOption.class));
    }

    @Test
    @DisplayName("음수 차감 시 예외 발생")
    void deductStock_negativeAmount_throwsException() {
        // given
        long optionId = 3L;
        ProductOption option = new ProductOption(optionId, 2L, "옵션", ProductOptionStatus.ON_SALE, 10000L, 10, LocalDateTime.now(), null);
        when(productOptionRepository.findByOptionId(optionId)).thenReturn(option);

        // when & then
        assertThatThrownBy(() -> deductStockService.deductStock(optionId, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("차감량은 음수일 수 없습니다");
        verify(productOptionRepository, times(1)).findByOptionId(optionId);
        verify(productOptionRepository, never()).insertOrUpdate(any(ProductOption.class));
    }
} 