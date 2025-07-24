package kr.hhplus.be.server.order.application.service;

import kr.hhplus.be.server.coupon.domain.model.UserCoupon;
import kr.hhplus.be.server.coupon.domain.repository.UserCouponRepository;
import kr.hhplus.be.server.coupon.domain.type.CouponPolicyType;
import kr.hhplus.be.server.coupon.domain.type.UserCouponStatus;
import kr.hhplus.be.server.order.application.dto.SaveOrderCommand;
import kr.hhplus.be.server.order.application.dto.SaveOrderItemCommand;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SaveOrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private UserCouponRepository userCouponRepository;
    @InjectMocks
    private SaveOrderService saveOrderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        saveOrderService = new SaveOrderService(orderRepository, orderItemRepository, userCouponRepository);
    }

    @Test
    @DisplayName("쿠폰 없이 주문 저장 성공")
    void saveOrder_noCoupon_success() {
        // given
        SaveOrderItemCommand item = new SaveOrderItemCommand(1L, 2L, "상품A", 10000L, null, 2);
        SaveOrderCommand command = new SaveOrderCommand(10L, List.of(item));
        doNothing().when(orderRepository).save(any(Order.class));
        doNothing().when(orderItemRepository).saveAll(any());

        // when
        long orderId = saveOrderService.save(command);

        // then
        assertThat(orderId).isGreaterThan(0L);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderItemRepository, times(1)).saveAll(any());
    }

    @Test
    @DisplayName("FIXED 쿠폰 적용 주문 저장 성공")
    void saveOrder_fixedCoupon_success() {
        // given
        long couponId = 100L;
        SaveOrderItemCommand item = new SaveOrderItemCommand(1L, 2L, "상품B", 20000L, couponId, 1);
        SaveOrderCommand command = new SaveOrderCommand(20L, List.of(item));
        UserCoupon coupon = new UserCoupon(10L, 0L, 20L, 0L, UserCouponStatus.ISSUED, CouponPolicyType.FIXED, null, 3000L, 10000L, 30, LocalDateTime.now().plusDays(30));
        when(userCouponRepository.selectByUserCouponId(couponId)).thenReturn(Optional.of(coupon));
        doNothing().when(orderRepository).save(any(Order.class));
        doNothing().when(orderItemRepository).saveAll(any());

        // when
        long orderId = saveOrderService.save(command);

        // then
        assertThat(orderId).isGreaterThan(0L);
        verify(userCouponRepository, times(1)).selectByUserCouponId(couponId);
    }

    @Test
    @DisplayName("RATE 쿠폰 적용 주문 저장 성공")
    void saveOrder_rateCoupon_success() {
        // given
        long couponId = 200L;
        SaveOrderItemCommand item = new SaveOrderItemCommand(1L, 2L, "상품C", 30000L, couponId, 1);
        SaveOrderCommand command = new SaveOrderCommand(30L, List.of(item));
        UserCoupon coupon = new UserCoupon(11L, 0L, 30L, 0L, UserCouponStatus.ISSUED, CouponPolicyType.RATE, 10.0f, null, 10000L, 30, LocalDateTime.now().plusDays(30));
        when(userCouponRepository.selectByUserCouponId(couponId)).thenReturn(Optional.of(coupon));
        doNothing().when(orderRepository).save(any(Order.class));
        doNothing().when(orderItemRepository).saveAll(any());

        // when
        long orderId = saveOrderService.save(command);

        // then
        assertThat(orderId).isGreaterThan(0L);
        verify(userCouponRepository, times(1)).selectByUserCouponId(couponId);
    }

    @Test
    @DisplayName("최소주문금액 미달 시 할인 미적용")
    void saveOrder_minimumOrderAmountNotMet_noDiscount() {
        // given
        long couponId = 300L;
        SaveOrderItemCommand item = new SaveOrderItemCommand(1L, 2L, "상품D", 5000L, couponId, 1);
        SaveOrderCommand command = new SaveOrderCommand(40L, List.of(item));
        UserCoupon coupon = new UserCoupon(12L, 0L, 40L, 0L, UserCouponStatus.ISSUED, CouponPolicyType.FIXED, null, 3000L, 10000L, 30, LocalDateTime.now().plusDays(30));
        when(userCouponRepository.selectByUserCouponId(couponId)).thenReturn(Optional.of(coupon));
        doNothing().when(orderRepository).save(any(Order.class));
        doNothing().when(orderItemRepository).saveAll(any());

        // when
        long orderId = saveOrderService.save(command);

        // then
        assertThat(orderId).isGreaterThan(0L);
        verify(userCouponRepository, times(1)).selectByUserCouponId(couponId);
    }
} 