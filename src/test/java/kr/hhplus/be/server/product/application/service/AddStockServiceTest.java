package kr.hhplus.be.server.product.application.service;

import kr.hhplus.be.server.common.exception.RestApiException;
import kr.hhplus.be.server.common.redis.cache.StockCounter;
import kr.hhplus.be.server.common.redis.cache.events.StockChangedEvent;
import kr.hhplus.be.server.product.application.usecase.AddStockUseCase;
import kr.hhplus.be.server.product.domain.model.ProductOption;
import kr.hhplus.be.server.product.domain.repository.ProductOptionRepository;
import kr.hhplus.be.server.product.exception.ProductErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddStockServiceTest {

    @Mock
    ProductOptionRepository productOptionRepository;

    @Mock
    ApplicationEventPublisher eventPublisher;

    @Mock
    StockCounter stockCounter;

    @InjectMocks
    AddStockService sut;

    @Test
    @DisplayName("정상 재고 증가 시 Redis와 DB 업데이트가 호출되고 이벤트가 발행된다")
    void addStock_success() {
        // given
        long optionId = 1L;
        long productId = 100L;
        int qty = 5;

        ProductOption mockOption = mock(ProductOption.class);
        when(mockOption.getProductId()).thenReturn(productId);
        when(productOptionRepository.findOptionByOptionId(optionId)).thenReturn(mockOption);
        doNothing().when(productOptionRepository).incrementStock(optionId, qty);

        // when & then
        assertThatCode(() -> sut.addStock(optionId, qty))
                .doesNotThrowAnyException();

        verify(productOptionRepository).findOptionByOptionId(optionId);
        verify(stockCounter).compensateHash(productId, optionId, qty);
        verify(productOptionRepository).incrementStock(optionId, qty);
        
        // 이벤트 발행 확인
        ArgumentCaptor<StockChangedEvent> eventCaptor = ArgumentCaptor.forClass(StockChangedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        
        StockChangedEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.productId()).isEqualTo(productId);
        assertThat(capturedEvent.optionId()).isEqualTo(optionId);
        assertThat(capturedEvent.changeType()).isEqualTo(StockChangedEvent.INCREMENT);
        assertThat(capturedEvent.quantity()).isEqualTo(qty);
    }

    @Test
    @DisplayName("재고 증가 실패 시 예외를 그대로 전달한다")
    void addStock_failure() {
        // given
        long optionId = 2L;
        long productId = 200L;
        int qty = 10;

        ProductOption mockOption = mock(ProductOption.class);
        when(mockOption.getProductId()).thenReturn(productId);
        when(productOptionRepository.findOptionByOptionId(optionId)).thenReturn(mockOption);
        doThrow(new RestApiException(ProductErrorCode.INVALID_QUANTITY_ERROR))
                .when(productOptionRepository).incrementStock(optionId, qty);

        // when & then
        assertThatThrownBy(() -> sut.addStock(optionId, qty))
                .isInstanceOf(RestApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", ProductErrorCode.INVALID_QUANTITY_ERROR);

        verify(productOptionRepository).findOptionByOptionId(optionId);
        verify(stockCounter).compensateHash(productId, optionId, qty);
        verify(productOptionRepository).incrementStock(optionId, qty);
        verifyNoMoreInteractions(productOptionRepository);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    @DisplayName("옵션을 찾을 수 없을 때 예외를 발생시킨다")
    void addStock_optionNotFound() {
        // given
        long optionId = 999L;
        int qty = 5;

        when(productOptionRepository.findOptionByOptionId(optionId))
                .thenThrow(new RestApiException(ProductErrorCode.OPTION_NOT_FOUND_ERROR));

        // when & then
        assertThatThrownBy(() -> sut.addStock(optionId, qty))
                .isInstanceOf(RestApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", ProductErrorCode.OPTION_NOT_FOUND_ERROR);

        verify(productOptionRepository).findOptionByOptionId(optionId);
        verifyNoMoreInteractions(productOptionRepository);
        verifyNoInteractions(stockCounter, eventPublisher);
    }

    @Test
    @DisplayName("음수 증가 시 유효성 예외를 그대로 전달한다")
    void addStock_negative() {
        // given
        long optionId = 3L;
        int qty = -1;

        // when & then
        assertThatThrownBy(() -> sut.addStock(optionId, qty))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(productOptionRepository, stockCounter, eventPublisher);
    }
}
