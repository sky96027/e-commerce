package kr.hhplus.be.server.product.application.service;

import kr.hhplus.be.server.product.domain.repository.ProductOptionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeductStockServiceTest {

    @Mock
    ProductOptionRepository productOptionRepository;

    @InjectMocks
    DeductStockService sut;

    @Test
    @DisplayName("정상 재고 차감 시 레포지토리의 원자적 UPDATE가 1회 호출된다")
    void deductStock_success() {
        // given
        long optionId = 1L;
        int qty = 2;

        when(productOptionRepository.decrementStock(optionId, qty)).thenReturn(true);

        // when & then
        assertThatCode(() -> sut.deductStock(optionId, qty))
                .doesNotThrowAnyException();

        verify(productOptionRepository).decrementStock(optionId, qty);
        verifyNoMoreInteractions(productOptionRepository);
    }

    @Test
    @DisplayName("재고 부족 시 예외를 그대로 전달한다")
    void deductStock_insufficient() {
        // given
        long optionId = 2L;
        int qty = 100;

        doThrow(new IllegalStateException("재고 부족"))
                .when(productOptionRepository).decrementStock(optionId, qty);

        // when & then
        assertThatThrownBy(() -> sut.deductStock(optionId, qty))
                .isInstanceOf(IllegalStateException.class);

        verify(productOptionRepository).decrementStock(optionId, qty);
        verifyNoMoreInteractions(productOptionRepository);
    }

    @Test
    @DisplayName("음수 차감 시 유효성 예외를 그대로 전달한다")
    void deductStock_negative() {
        // given
        long optionId = 3L;
        int qty = -1;

        // when & then
        assertThatThrownBy(() -> sut.deductStock(optionId, qty))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoMoreInteractions(productOptionRepository);
    }
}
