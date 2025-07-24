package kr.hhplus.be.server.order.application.service;

import kr.hhplus.be.server.order.application.dto.OrderDto;
import kr.hhplus.be.server.order.domain.model.Order;
import kr.hhplus.be.server.order.domain.model.OrderItem;
import kr.hhplus.be.server.order.domain.repository.OrderItemRepository;
import kr.hhplus.be.server.order.domain.repository.OrderRepository;
import kr.hhplus.be.server.order.domain.type.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ChangeOrderStatusServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @InjectMocks
    private ChangeOrderStatusService changeOrderStatusService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        changeOrderStatusService = new ChangeOrderStatusService(orderRepository, orderItemRepository);
    }

    @Test
    @DisplayName("주문 상태 정상 변경")
    void changeStatus_success() {
        // given
        long orderId = 1L;
        Order order = new Order(orderId, 2L, 30000L, 5000L, OrderStatus.BEFORE_PAYMENT, LocalDateTime.now());
        List<OrderItem> items = List.of(
                new OrderItem(10L, orderId, 100L, 200L, "상품A", 10000L, 0L, null, 1)
        );
        when(orderRepository.selectByOrderId(orderId)).thenReturn(order);
        when(orderItemRepository.selectByOrderId(orderId)).thenReturn(items);
        doNothing().when(orderRepository).save(any(Order.class));

        // when
        OrderDto result = changeOrderStatusService.changeStatus(orderId, OrderStatus.AFTER_PAYMENT);

        // then
        assertThat(result.orderId()).isEqualTo(orderId);
        assertThat(result.status()).isEqualTo(OrderStatus.AFTER_PAYMENT.name());
        verify(orderRepository, times(1)).selectByOrderId(orderId);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderItemRepository, times(1)).selectByOrderId(orderId);
    }

    @Test
    @DisplayName("존재하지 않는 주문 ID로 상태 변경 시 예외 발생")
    void changeStatus_notFound_throwsException() {
        // given
        long orderId = 2L;
        when(orderRepository.selectByOrderId(orderId)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> changeOrderStatusService.changeStatus(orderId, OrderStatus.AFTER_PAYMENT))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 주문이 존재하지 않습니다");
        verify(orderRepository, times(1)).selectByOrderId(orderId);
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderItemRepository, never()).selectByOrderId(anyLong());
    }
} 