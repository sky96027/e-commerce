package kr.hhplus.be.server.order.integration;

import kr.hhplus.be.server.coupon.domain.type.CouponPolicyType;
import kr.hhplus.be.server.coupon.domain.type.UserCouponStatus;
import kr.hhplus.be.server.coupon.infrastructure.entity.UserCouponJpaEntity;
import kr.hhplus.be.server.coupon.infrastructure.repository.UserCouponJpaRepository;
import kr.hhplus.be.server.order.application.dto.SaveOrderCommand;
import kr.hhplus.be.server.order.application.dto.SaveOrderItemCommand;
import kr.hhplus.be.server.order.application.service.SaveOrderService;
import kr.hhplus.be.server.order.infrastructure.entity.OrderItemJpaEntity;
import kr.hhplus.be.server.order.infrastructure.entity.OrderJpaEntity;
import kr.hhplus.be.server.order.infrastructure.repository.OrderItemJpaRepository;
import kr.hhplus.be.server.order.infrastructure.repository.OrderJpaRepository;
import kr.hhplus.be.server.order.domain.type.OrderStatus;
import kr.hhplus.be.server.user.infrastructure.entity.UserJpaEntity;
import kr.hhplus.be.server.user.infrastructure.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DisplayName("통합 테스트 - 주문 저장 서비스")
public class SaveOrderIntegrationTest {

    @Autowired
    private SaveOrderService saveOrderService;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private UserCouponJpaRepository userCouponJpaRepository;

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Autowired
    private OrderItemJpaRepository orderItemJpaRepository;

    private UserJpaEntity testUser;
    private UserCouponJpaEntity testCoupon;

    @BeforeEach
    void setUp() {
        // 사용자 생성
        testUser = new UserJpaEntity(50000L);
        testUser = userJpaRepository.save(testUser);

        // 쿠폰 생성 (할인율 10%)
        testCoupon = new UserCouponJpaEntity(
                null,
                1L, // couponId
                testUser.getUserId(),
                100L, // policyId
                UserCouponStatus.ISSUED,
                CouponPolicyType.RATE,
                10.0f,
                30,
                LocalDateTime.now().plusDays(30)
        );
        testCoupon = userCouponJpaRepository.save(testCoupon);
    }

    @Test
    @DisplayName("쿠폰을 포함한 주문이 정상 저장된다")
    void saveOrder_withCoupon_success() {
        // given
        long productId = 100L;
        long optionId = 200L;

        SaveOrderItemCommand item = new SaveOrderItemCommand(
                productId,
                optionId,
                "테스트상품",
                10000L,
                testCoupon.getUserCouponId(),
                2 // 수량 2 → 총액 20000 → 할인 10% → 2000원
        );

        SaveOrderCommand command = new SaveOrderCommand(
                testUser.getUserId(),
                List.of(item)
        );

        // when
        long orderId = saveOrderService.save(command);

        // then
        OrderJpaEntity savedOrder = orderJpaRepository.findById(orderId).orElseThrow();
        assertThat(savedOrder.getUserId()).isEqualTo(testUser.getUserId());
        assertThat(savedOrder.getTotalAmount()).isEqualTo(20000L);
        assertThat(savedOrder.getTotalDiscountAmount()).isEqualTo(2000L);
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.BEFORE_PAYMENT);

        List<OrderItemJpaEntity> items = orderItemJpaRepository.findAllByOrderId(orderId);
        assertThat(items).hasSize(1);
        OrderItemJpaEntity savedItem = items.get(0);
        assertThat(savedItem.getProductId()).isEqualTo(productId);
        assertThat(savedItem.getUserCouponId()).isEqualTo(testCoupon.getUserCouponId());
        assertThat(savedItem.getDiscountAmount()).isEqualTo(2000L);
    }
}
