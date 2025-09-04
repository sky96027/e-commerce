package kr.hhplus.be.server.payment.integration;

import kr.hhplus.be.server.IntegrationTestBase;
import kr.hhplus.be.server.coupon.infrastructure.entity.UserCouponJpaEntity;
import kr.hhplus.be.server.coupon.infrastructure.repository.UserCouponJpaRepository;
import kr.hhplus.be.server.order.domain.type.OrderStatus;
import kr.hhplus.be.server.order.infrastructure.entity.OrderJpaEntity;
import kr.hhplus.be.server.order.infrastructure.entity.OrderItemJpaEntity;
import kr.hhplus.be.server.order.infrastructure.repository.OrderJpaRepository;
import kr.hhplus.be.server.order.infrastructure.repository.OrderItemJpaRepository;
import kr.hhplus.be.server.payment.application.dto.PaymentDto;
import kr.hhplus.be.server.payment.application.event.dto.PaymentCompletedEvent;
import kr.hhplus.be.server.payment.application.facade.PaymentFacade;
import kr.hhplus.be.server.payment.application.kafka.producer.PaymentEventProducer;
import kr.hhplus.be.server.payment.application.usecase.FindByOrderIdUseCase;
import kr.hhplus.be.server.payment.domain.type.PaymentStatus;
import kr.hhplus.be.server.product.domain.type.ProductOptionStatus;
import kr.hhplus.be.server.product.domain.type.ProductStatus;
import kr.hhplus.be.server.product.infrastructure.entity.ProductJpaEntity;
import kr.hhplus.be.server.product.infrastructure.entity.ProductOptionJpaEntity;
import kr.hhplus.be.server.product.infrastructure.repository.ProductJpaRepository;
import kr.hhplus.be.server.product.infrastructure.repository.ProductOptionJpaRepository;
import kr.hhplus.be.server.user.infrastructure.entity.UserJpaEntity;
import kr.hhplus.be.server.user.infrastructure.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
@DisplayName("통합 테스트 - 결제 Facade")
class PaymentFacadeIntegrationTest extends IntegrationTestBase {

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
    private UserCouponJpaRepository userCouponJpaRepository;
  
    @MockBean
    private PaymentEventProducer paymentEventProducer;

    private UserJpaEntity testUser;
    private ProductJpaEntity testProduct;
    private ProductOptionJpaEntity testProductOption;
    private OrderJpaEntity testOrder;
    private OrderItemJpaEntity testOrderItem;

    @BeforeEach
    void setUp() {
        createTestData();
    }

    private void createTestData() {
        // 1. 사용자 생성
        testUser = new UserJpaEntity(100000L);
        testUser = userJpaRepository.save(testUser);

        // 2. 상품 생성
        testProduct = new ProductJpaEntity(
                null,
                "테스트상품",
                ProductStatus.ON_SALE,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30)
        );
        testProduct = productJpaRepository.save(testProduct);

        // 3. 상품 옵션 생성
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

        // 4. 주문 생성
        testOrder = new OrderJpaEntity(
                null,
                testUser.getUserId(),
                50000L,
                5000L,
                OrderStatus.BEFORE_PAYMENT,
                LocalDateTime.now()
        );
        testOrder = orderJpaRepository.save(testOrder);

        // 5. 주문 아이템 생성
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
    @DisplayName("PaymentFacade가 정상적으로 결제를 처리하고 Kafka 이벤트를 발행한다")
    void paymentFacade_processPayment_success_and_publishEvent() {
        // given
        long orderId = testOrder.getOrderId();
        long userId = testUser.getUserId();
        long optionId = testProductOption.getOptionId();
        long originalUserBalance = testUser.getBalance();
        int originalStock = testProductOption.getStock();
        long totalAmount = testOrder.getTotalAmount();

        // when
        long paymentId = paymentFacade.processPayment(orderId);

        // then
        // 결제 정보 확인
        PaymentDto payment = findByOrderIdUseCase.findByOrderId(orderId);
        assertThat(payment).isNotNull();
        assertThat(payment.orderId()).isEqualTo(orderId);
        assertThat(payment.userId()).isEqualTo(userId);
        assertThat(payment.totalAmountSnapshot()).isEqualTo(totalAmount);
        assertThat(payment.status()).isEqualTo(PaymentStatus.AFTER_PAYMENT);

        // 잔액 차감 확인
        UserJpaEntity updatedUser = userJpaRepository.findById(userId).orElseThrow();
        assertThat(updatedUser.getBalance()).isEqualTo(originalUserBalance - totalAmount);

        // 재고 차감 확인
        ProductOptionJpaEntity updatedOption = productOptionJpaRepository.findById(optionId).orElseThrow();
        assertThat(updatedOption.getStock()).isEqualTo(originalStock - 1);

        // 주문 상태 변경 확인
        OrderJpaEntity updatedOrder = orderJpaRepository.findById(orderId).orElseThrow();
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.AFTER_PAYMENT);

        verify(paymentEventProducer).send(
                new PaymentCompletedEvent(userId, paymentId, totalAmount)
        );
    }
}
