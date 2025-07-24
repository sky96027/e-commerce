package kr.hhplus.be.server.order.domain.model;

import kr.hhplus.be.server.order.domain.type.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {
    @Test
    @DisplayName("주문 상태 변경(changeStatus) 정상 동작")
    void changeStatus_success() {
        // given
        Order order = new Order(
                1L, 2L, 30000L, 5000L, OrderStatus.BEFORE_PAYMENT, LocalDateTime.now()
        );
        // when
        Order updated = order.changeStatus(OrderStatus.AFTER_PAYMENT);
        // then
        assertThat(updated.getStatus()).isEqualTo(OrderStatus.AFTER_PAYMENT);
        assertThat(updated.getOrderId()).isEqualTo(order.getOrderId());
        assertThat(updated.getUserId()).isEqualTo(order.getUserId());
        assertThat(updated.getTotalAmount()).isEqualTo(order.getTotalAmount());
        assertThat(updated.getTotalDiscountAmount()).isEqualTo(order.getTotalDiscountAmount());
        assertThat(updated.getOrderAt()).isEqualTo(order.getOrderAt());
    }
} 