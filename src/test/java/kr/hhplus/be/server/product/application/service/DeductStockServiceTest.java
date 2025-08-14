package kr.hhplus.be.server.product.application.service;

import kr.hhplus.be.server.common.redis.cache.events.StockChangedEvent;
import kr.hhplus.be.server.common.redis.cache.StockCounter;
import kr.hhplus.be.server.product.domain.model.ProductOption;
import kr.hhplus.be.server.product.domain.repository.ProductOptionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeductStockServiceTest {

    @Mock
    ProductOptionRepository productOptionRepository;

    @Mock
    ApplicationEventPublisher eventPublisher;

    @Mock
    StockCounter stockCounter;

    @InjectMocks
    DeductStockService sut;

    @Test
    @DisplayName("정상 재고 차감 시 Redis와 DB 업데이트가 호출되고 이벤트가 발행된다")
    void deductStock_success() {
        // given
        long optionId = 1L;
        long productId = 100L;
        int qty = 2;

        ProductOption mockOption = mock(ProductOption.class);
        when(mockOption.getProductId()).thenReturn(productId);
        when(productOptionRepository.findOptionByOptionId(optionId)).thenReturn(mockOption);
        when(stockCounter.tryDeductHash(productId, optionId, qty)).thenReturn(8L); // 성공 시 남은 재고 반환
        when(productOptionRepository.decrementStock(optionId, qty)).thenReturn(true); // DB 성공

        // when & then
        assertThatCode(() -> sut.deductStock(optionId, qty))
                .doesNotThrowAnyException();

        verify(productOptionRepository).findOptionByOptionId(optionId);
        verify(stockCounter).tryDeductHash(productId, optionId, qty);
        verify(productOptionRepository).decrementStock(optionId, qty); // DB 업데이트 호출 확인
        
        // 이벤트 발행 확인
        ArgumentCaptor<StockChangedEvent> eventCaptor = ArgumentCaptor.forClass(StockChangedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        
        StockChangedEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.productId()).isEqualTo(productId);
        assertThat(capturedEvent.optionId()).isEqualTo(optionId);
        assertThat(capturedEvent.changeType()).isEqualTo(StockChangedEvent.DEDUCT);
        assertThat(capturedEvent.quantity()).isEqualTo(qty);
    }

    @Test
    @DisplayName("Redis 재고 부족 시 DB에서 재확인 및 차감 후 이벤트 발행")
    void deductStock_redisInsufficient_dbSuccess() {
        // given
        long optionId = 2L;
        long productId = 200L;
        int qty = 100;

        ProductOption mockOption = mock(ProductOption.class);
        when(mockOption.getProductId()).thenReturn(productId);
        when(productOptionRepository.findOptionByOptionId(optionId)).thenReturn(mockOption);
        when(stockCounter.tryDeductHash(productId, optionId, qty)).thenReturn(-1L); // Redis 재고 부족
        when(productOptionRepository.decrementStock(optionId, qty)).thenReturn(true); // DB 성공
        when(mockOption.getStock()).thenReturn(50); // DB 조회 후 남은 재고

        // when & then
        assertThatCode(() -> sut.deductStock(optionId, qty))
                .doesNotThrowAnyException();

        verify(stockCounter).tryDeductHash(productId, optionId, qty);
        verify(productOptionRepository).decrementStock(optionId, qty);
        verify(productOptionRepository, times(2)).findOptionByOptionId(optionId); // 조회 2회 (처음 + DB 성공 후)
        verify(stockCounter).initStockHash(productId, optionId, 50);
        
        // 이벤트 발행 확인
        ArgumentCaptor<StockChangedEvent> eventCaptor = ArgumentCaptor.forClass(StockChangedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        
        StockChangedEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.productId()).isEqualTo(productId);
        assertThat(capturedEvent.optionId()).isEqualTo(optionId);
        assertThat(capturedEvent.changeType()).isEqualTo(StockChangedEvent.DEDUCT);
        assertThat(capturedEvent.quantity()).isEqualTo(qty);
    }

    @Test
    @DisplayName("재고 부족 시 예외를 그대로 전달한다")
    void deductStock_insufficient() {
        // given
        long optionId = 2L;
        long productId = 200L;
        int qty = 100;

        ProductOption mockOption = mock(ProductOption.class);
        when(mockOption.getProductId()).thenReturn(productId);
        when(productOptionRepository.findOptionByOptionId(optionId)).thenReturn(mockOption);
        when(stockCounter.tryDeductHash(productId, optionId, qty)).thenReturn(-1L); // Redis 재고 부족
        when(productOptionRepository.decrementStock(optionId, qty)).thenReturn(false); // DB도 실패

        // when & then
        assertThatThrownBy(() -> sut.deductStock(optionId, qty))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("재고 부족");

        verify(productOptionRepository).findOptionByOptionId(optionId);
        verify(stockCounter).tryDeductHash(productId, optionId, qty);
        verify(productOptionRepository).decrementStock(optionId, qty);
        verifyNoMoreInteractions(productOptionRepository);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    @DisplayName("옵션을 찾을 수 없을 때 예외를 발생시킨다")
    void deductStock_optionNotFound() {
        // given
        long optionId = 999L;
        int qty = 2;

        when(productOptionRepository.findOptionByOptionId(optionId)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> sut.deductStock(optionId, qty))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("옵션을 찾을 수 없음");

        verify(productOptionRepository).findOptionByOptionId(optionId);
        verifyNoMoreInteractions(productOptionRepository);
        verifyNoInteractions(stockCounter, eventPublisher);
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

        verifyNoInteractions(productOptionRepository, stockCounter, eventPublisher);
    }
}
