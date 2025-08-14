package kr.hhplus.be.server.order.integration;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.IntegrationTestBase;
import kr.hhplus.be.server.order.application.dto.OrderDto;
import kr.hhplus.be.server.order.application.service.ChangeOrderStatusService;
import kr.hhplus.be.server.order.domain.type.OrderStatus;
import kr.hhplus.be.server.order.infrastructure.entity.OrderItemJpaEntity;
import kr.hhplus.be.server.order.infrastructure.entity.OrderJpaEntity;
import kr.hhplus.be.server.order.infrastructure.repository.OrderItemJpaRepository;
import kr.hhplus.be.server.order.infrastructure.repository.OrderJpaRepository;
import kr.hhplus.be.server.product.domain.type.ProductStatus;
import kr.hhplus.be.server.user.infrastructure.entity.UserJpaEntity;
import kr.hhplus.be.server.user.infrastructure.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@DisplayName("통합 테스트 - 주문 상태 변경")
public class ChangeOrderStatusIntegrationTest extends IntegrationTestBase {

    @Autowired
    private ChangeOrderStatusService changeOrderStatusService;

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private EntityManager entityManager;

    private OrderJpaEntity testOrder;
    private OrderItemJpaEntity testOrderItem;

    @BeforeEach
    void setUp() {
        // 1. 사용자 생성
        UserJpaEntity user = new UserJpaEntity(50000L);
        user = userJpaRepository.save(user);

        // 2. 주문 생성
        testOrder = new OrderJpaEntity(
                null,
                user.getUserId(),
                30000L,
                0L,
                OrderStatus.BEFORE_PAYMENT,
                LocalDateTime.now()
        );
        testOrder = orderJpaRepository.save(testOrder);
    }

    @Test
    @DisplayName("주문 상태를 AFTER_PAYMENT로 변경한다")
    void changeOrderStatus_success() {
        // given
        long orderId = testOrder.getOrderId();

        // when
        OrderDto result = changeOrderStatusService.changeStatus(orderId, OrderStatus.AFTER_PAYMENT);

        // then
        assertThat(result).isNotNull();
        assertThat(result.orderId()).isEqualTo(orderId);
        assertThat(result.status()).isEqualTo(OrderStatus.AFTER_PAYMENT);

        /*entityManager.flush();
        entityManager.clear();*/

        // 실제 DB에 반영되었는지 확인
        OrderJpaEntity updated = orderJpaRepository.findById(orderId).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(OrderStatus.AFTER_PAYMENT);
    }
}
