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
import static org.mockito.Mockito.*;

class FindOrderByOrderIdServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @InjectMocks
    private FindOrderByOrderIdService findOrderByOrderIdService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        findOrderByOrderIdService = new FindOrderByOrderIdService(orderRepository, orderItemRepository);
    }

    @Test
    @DisplayName("주문 ID로 정상 조회")
    void findById_success() {
        // given
        long orderId = 1L;
        Order order = new Order(orderId, 2L, 30000L, 5000L, OrderStatus.BEFORE_PAYMENT, LocalDateTime.now());
        List<OrderItem> items = List.of(
                new OrderItem(10L, orderId, 100L, 200L, "상품A", 10000L, 0L, null, 1)
        );
        when(orderRepository.findById(orderId)).thenReturn(order);
        when(orderItemRepository.findAllByOrderId(orderId)).thenReturn(items);

        // when
        OrderDto result = findOrderByOrderIdService.findById(orderId);

        // then
        assertThat(result.orderId()).isEqualTo(orderId);
        assertThat(result.items()).hasSize(1);
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderItemRepository, times(1)).findAllByOrderId(orderId);
    }

} 