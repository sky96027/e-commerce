package kr.hhplus.be.server.order.integration;

import kr.hhplus.be.server.IntegrationTestBase;
import kr.hhplus.be.server.order.application.dto.OrderItemDto;
import kr.hhplus.be.server.order.application.service.FindOrderItemByOrderIdService;
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
@DisplayName("통합 테스트 - 주문 아이템 단건/다건 조회")
public class FindOrderItemIntegrationTest extends IntegrationTestBase {

    @Autowired
    private FindOrderItemByOrderIdService findOrderItemByOrderIdService;

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Autowired
    private OrderItemJpaRepository orderItemJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    private OrderJpaEntity testOrder;

    @BeforeEach
    void setUp() {
        // 사용자 (필요 시)
        UserJpaEntity user = new UserJpaEntity(50000L);
        user = userJpaRepository.save(user);

        // 주문 생성
        testOrder = new OrderJpaEntity(
                null,
                user.getUserId(),
                40000L,
                2000L,
                OrderStatus.BEFORE_PAYMENT,
                LocalDateTime.now()
        );
        testOrder = orderJpaRepository.save(testOrder);

        // 주문 아이템 2개 생성
        OrderItemJpaEntity item1 = new OrderItemJpaEntity(
                null,
                testOrder.getOrderId(),
                10L,
                100L,
                "상품A",
                20000L,
                1000L,
                null,
                1
        );
        OrderItemJpaEntity item2 = new OrderItemJpaEntity(
                null,
                testOrder.getOrderId(),
                11L,
                101L,
                "상품B",
                20000L,
                1000L,
                null,
                1
        );
        orderItemJpaRepository.save(item1);
        orderItemJpaRepository.save(item2);
    }

    @Test
    @DisplayName("주문 ID로 주문 아이템들을 조회한다")
    void findOrderItemsByOrderId_success() {
        // given
        long orderId = testOrder.getOrderId();

        // when
        List<OrderItemDto> items = findOrderItemByOrderIdService.findByOrderId(orderId);

        // then
        assertThat(items).hasSize(2);

        OrderItemDto item1 = items.get(0);
        assertThat(item1.productName()).isIn("상품A", "상품B");
        assertThat(item1.quantity()).isEqualTo(1);
        assertThat(item1.orderId()).isEqualTo(orderId);
    }
}
