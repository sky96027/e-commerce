package kr.hhplus.be.server.order.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderItemTest {
    @Test
    @DisplayName("OrderItem 생성자 및 필드 값 정상 할당")
    void constructor_success() {
        // given
        long orderItemId = 1L;
        long orderId = 2L;
        long productId = 3L;
        long optionId = 4L;
        String productName = "테스트상품";
        long productPrice = 10000L;
        long discountAmount = 2000L;
        Long userCouponId = 10L;
        int quantity = 2;

        // when
        OrderItem item = new OrderItem(orderItemId, orderId, productId, optionId, productName, productPrice, discountAmount, userCouponId, quantity);

        // then
        assertThat(item.getOrderItemId()).isEqualTo(orderItemId);
        assertThat(item.getOrderId()).isEqualTo(orderId);
        assertThat(item.getProductId()).isEqualTo(productId);
        assertThat(item.getOptionId()).isEqualTo(optionId);
        assertThat(item.getProductName()).isEqualTo(productName);
        assertThat(item.getProductPrice()).isEqualTo(productPrice);
        assertThat(item.getDiscountAmount()).isEqualTo(discountAmount);
        assertThat(item.getUserCouponId()).isEqualTo(userCouponId);
        assertThat(item.getQuantity()).isEqualTo(quantity);
    }
} 