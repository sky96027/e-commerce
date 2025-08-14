package kr.hhplus.be.server.order.integration;

import kr.hhplus.be.server.IntegrationTestBase;
import kr.hhplus.be.server.order.application.dto.OrderDto;
import kr.hhplus.be.server.order.application.dto.OrderItemDto;
import kr.hhplus.be.server.order.application.service.FindOrderByOrderIdService;
import kr.hhplus.be.server.order.domain.type.OrderStatus;
import kr.hhplus.be.server.order.infrastructure.entity.OrderItemJpaEntity;
import kr.hhplus.be.server.order.infrastructure.entity.OrderJpaEntity;
import kr.hhplus.be.server.order.infrastructure.repository.OrderItemJpaRepository;
import kr.hhplus.be.server.order.infrastructure.repository.OrderJpaRepository;
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

@Transactional
@DisplayName("통합 테스트 - 주문 단건 조회")
public class FindOrderIntegrationTest extends IntegrationTestBase {

    @Autowired
    private FindOrderByOrderIdService findOrderByOrderIdService;

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Autowired
    private OrderItemJpaRepository orderItemJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    private OrderJpaEntity testOrder;
    private OrderItemJpaEntity testOrderItem;
    private UserJpaEntity testUser;

    @BeforeEach
    void setUp() {
        // 1. 사용자 생성
        testUser = new UserJpaEntity(50000L);
        testUser = userJpaRepository.save(testUser);

        // 2. 주문 생성
        testOrder = new OrderJpaEntity(
                null,
                testUser.getUserId(),
                30000L,
                0L,
                OrderStatus.BEFORE_PAYMENT,
                LocalDateTime.now()
        );
        testOrder = orderJpaRepository.save(testOrder);

        // 3. 주문 아이템 생성
        testOrderItem = new OrderItemJpaEntity(
                null,
                testOrder.getOrderId(),
                1L, // productId
                2L, // optionId
                "테스트상품",
                30000L,
                0L,
                null,
                1
        );
        testOrderItem = orderItemJpaRepository.save(testOrderItem);
    }

    @Test
    @DisplayName("주문과 주문 아이템을 정상적으로 조회한다")
    void findOrderById_success() {
        // given
        long orderId = testOrder.getOrderId();

        // when
        OrderDto result = findOrderByOrderIdService.findById(orderId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.orderId()).isEqualTo(orderId);
        assertThat(result.userId()).isEqualTo(testUser.getUserId());
        assertThat(result.totalAmount()).isEqualTo(30000L);
        assertThat(result.status()).isEqualTo(OrderStatus.BEFORE_PAYMENT);

        List<OrderItemDto> items = result.items();
        assertThat(items).hasSize(1);
        OrderItemDto item = items.get(0);
        assertThat(item.productId()).isEqualTo(1L);
        assertThat(item.optionId()).isEqualTo(2L);
        assertThat(item.quantity()).isEqualTo(1);
        assertThat(item.productName()).isEqualTo("테스트상품");
    }
}
