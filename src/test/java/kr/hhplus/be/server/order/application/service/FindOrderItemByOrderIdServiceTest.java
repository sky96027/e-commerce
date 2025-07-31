package kr.hhplus.be.server.order.application.service;

import kr.hhplus.be.server.order.application.dto.OrderItemDto;
import kr.hhplus.be.server.order.domain.model.OrderItem;
import kr.hhplus.be.server.order.domain.repository.OrderItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class FindOrderItemByOrderIdServiceTest {
    @Mock
    private OrderItemRepository orderItemRepository;
    @InjectMocks
    private FindOrderItemByOrderIdService findOrderItemByOrderIdService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        findOrderItemByOrderIdService = new FindOrderItemByOrderIdService(orderItemRepository);
    }

    @Test
    @DisplayName("주문 ID로 주문 아이템 정상 조회")
    void findByOrderId_success() {
        // given
        long orderId = 1L;
        List<OrderItem> items = List.of(
                new OrderItem(10L, orderId, 100L, 200L, "상품A", 10000L, 0L, null, 1),
                new OrderItem(11L, orderId, 101L, 201L, "상품B", 20000L, 1000L, 1L, 2)
        );
        when(orderItemRepository.findAllByOrderId(orderId)).thenReturn(items);

        // when
        List<OrderItemDto> result = findOrderItemByOrderIdService.findByOrderId(orderId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).orderItemId()).isEqualTo(10L);
        assertThat(result.get(1).orderItemId()).isEqualTo(11L);
        verify(orderItemRepository, times(1)).findAllByOrderId(orderId);
    }

    @Test
    @DisplayName("주문 ID로 조회 시 아이템이 없으면 빈 목록 반환")
    void findByOrderId_empty() {
        // given
        long orderId = 2L;
        when(orderItemRepository.findAllByOrderId(orderId)).thenReturn(Collections.emptyList());

        // when
        List<OrderItemDto> result = findOrderItemByOrderIdService.findByOrderId(orderId);

        // then
        assertThat(result).isEmpty();
        verify(orderItemRepository, times(1)).findAllByOrderId(orderId);
    }
} 