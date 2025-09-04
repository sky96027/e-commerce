package kr.hhplus.be.server.payment.integration;

import kr.hhplus.be.server.IntegrationTestBase;
import kr.hhplus.be.server.order.domain.type.OrderStatus;
import kr.hhplus.be.server.order.infrastructure.entity.OrderJpaEntity;
import kr.hhplus.be.server.order.infrastructure.entity.OrderItemJpaEntity;
import kr.hhplus.be.server.order.infrastructure.repository.OrderJpaRepository;
import kr.hhplus.be.server.order.infrastructure.repository.OrderItemJpaRepository;
import kr.hhplus.be.server.payment.application.dto.PaymentDto;
import kr.hhplus.be.server.payment.application.facade.PaymentFacade;
import kr.hhplus.be.server.payment.application.usecase.FindByOrderIdUseCase;
import kr.hhplus.be.server.payment.domain.type.PaymentStatus;
import kr.hhplus.be.server.product.domain.type.ProductOptionStatus;
import kr.hhplus.be.server.product.domain.type.ProductStatus;
import kr.hhplus.be.server.product.infrastructure.entity.ProductJpaEntity;
import kr.hhplus.be.server.product.infrastructure.entity.ProductOptionJpaEntity;
import kr.hhplus.be.server.product.infrastructure.repository.ProductJpaRepository;
import kr.hhplus.be.server.product.infrastructure.repository.ProductOptionJpaRepository;
import kr.hhplus.be.server.transactionhistory.infrastructure.entity.TransactionHistoryJpaEntity;
import kr.hhplus.be.server.transactionhistory.infrastructure.repository.TransactionHistoryJpaRepository;
import kr.hhplus.be.server.user.infrastructure.entity.UserJpaEntity;
import kr.hhplus.be.server.user.infrastructure.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import java.time.Duration;

@SpringBootTest
@Transactional
@DisplayName("통합 테스트 - 결제 후 Kafka 이벤트를 통해 거래내역 저장 ")
class PaymentFacadeKafkaIntegrationTest extends IntegrationTestBase {

    @Autowired
    private PaymentFacade paymentFacade;

    @Autowired
    private FindByOrderIdUseCase findByOrderIdUseCase;

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Autowired
    private OrderItemJpaRepository orderItemJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private ProductJpaRepository productJpaRepository;

    @Autowired
    private ProductOptionJpaRepository productOptionJpaRepository;

    @Autowired
    private TransactionHistoryJpaRepository transactionHistoryJpaRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private UserJpaEntity testUser;
    private ProductJpaEntity testProduct;
    private ProductOptionJpaEntity testProductOption;
    private OrderJpaEntity testOrder;
    private OrderItemJpaEntity testOrderItem;

    @BeforeEach
    void setUp() {
        createTestData();
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    private void createTestData() {
        testUser = new UserJpaEntity(100000L);
        testUser = userJpaRepository.save(testUser);

        testProduct = new ProductJpaEntity(
                null,
                "테스트상품",
                ProductStatus.ON_SALE,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30)
        );
        testProduct = productJpaRepository.save(testProduct);

        testProductOption = new ProductOptionJpaEntity(
                null,
                testProduct.getProductId(),
                "테스트옵션",
                ProductOptionStatus.ON_SALE,
                50000L,
                10,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30)
        );
        testProductOption = productOptionJpaRepository.save(testProductOption);

        testOrder = new OrderJpaEntity(
                null,
                testUser.getUserId(),
                50000L,
                5000L,
                OrderStatus.BEFORE_PAYMENT,
                LocalDateTime.now()
        );
        testOrder = orderJpaRepository.save(testOrder);

        testOrderItem = new OrderItemJpaEntity(
                null,
                testOrder.getOrderId(),
                testProduct.getProductId(),
                testProductOption.getOptionId(),
                "테스트상품",
                50000L,
                5000L,
                null,
                1
        );
        testOrderItem = orderItemJpaRepository.save(testOrderItem);
    }

    @Test
    @DisplayName("결제 완료 이벤트가 Kafka를 통해 발행되고, Consumer가 받아 거래내역이 저장된다")
    void paymentFacade_processPayment_publishAndConsume_success() {
        // given
        long orderId = testOrder.getOrderId();
        long userId = testUser.getUserId();
        long totalAmount = testOrder.getTotalAmount();

        // when
        long paymentId = paymentFacade.processPayment(orderId);

        // then
        PaymentDto payment = findByOrderIdUseCase.findByOrderId(orderId);
        assertThat(payment).isNotNull();
        assertThat(payment.status()).isEqualTo(PaymentStatus.AFTER_PAYMENT);

        // Kafka Consumer 처리 대기 (최대 5초)
        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            List<TransactionHistoryJpaEntity> histories = transactionHistoryJpaRepository.findAll();
            assertThat(histories).isNotEmpty();
            assertThat(histories.get(0).getUserId()).isEqualTo(userId);
            assertThat(histories.get(0).getAmount()).isEqualTo(totalAmount);
        });
    }
}
