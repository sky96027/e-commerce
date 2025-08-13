package kr.hhplus.be.server.payment.integration;

import kr.hhplus.be.server.IntegrationTestBase;
import kr.hhplus.be.server.coupon.infrastructure.entity.UserCouponJpaEntity;
import kr.hhplus.be.server.coupon.infrastructure.repository.UserCouponJpaRepository;
import kr.hhplus.be.server.order.domain.type.OrderStatus;
import kr.hhplus.be.server.payment.application.facade.PaymentFacade;
import kr.hhplus.be.server.payment.application.dto.PaymentDto;
import kr.hhplus.be.server.payment.application.usecase.FindByOrderIdUseCase;
import kr.hhplus.be.server.payment.domain.type.PaymentStatus;
import kr.hhplus.be.server.product.domain.type.ProductOptionStatus;
import kr.hhplus.be.server.order.infrastructure.entity.OrderJpaEntity;
import kr.hhplus.be.server.order.infrastructure.entity.OrderItemJpaEntity;
import kr.hhplus.be.server.order.infrastructure.repository.OrderJpaRepository;
import kr.hhplus.be.server.order.infrastructure.repository.OrderItemJpaRepository;
import kr.hhplus.be.server.product.infrastructure.entity.ProductJpaEntity;
import kr.hhplus.be.server.product.infrastructure.entity.ProductOptionJpaEntity;
import kr.hhplus.be.server.product.infrastructure.repository.ProductJpaRepository;
import kr.hhplus.be.server.product.infrastructure.repository.ProductOptionJpaRepository;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@DisplayName("통합 테스트 - 결제 Facade")
public class PaymentFacadeIntegrationTest extends IntegrationTestBase {

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

    private UserJpaEntity testUser;
    private ProductJpaEntity testProduct;
    private ProductOptionJpaEntity testProductOption;
    private OrderJpaEntity testOrder;
    private OrderItemJpaEntity testOrderItem;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 생성
        createTestData();
    }

    private void createTestData() {
        // 1. 사용자 생성
        testUser = new UserJpaEntity(100000L); // 충분한 잔액
        testUser = userJpaRepository.save(testUser);

        // 2. 상품 생성
        testProduct = new ProductJpaEntity(
                null, 
                "테스트상품",
                kr.hhplus.be.server.product.domain.type.ProductStatus.ON_SALE,
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
                50000L, // 가격
                10, // 재고
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30)
        );
        testProductOption = productOptionJpaRepository.save(testProductOption);

        // 4. 주문 생성
        testOrder = new OrderJpaEntity(
                null,
                testUser.getUserId(),
                50000L, // 총 금액
                5000L, // 할인 금액
                OrderStatus.BEFORE_PAYMENT,
                LocalDateTime.now()
        );
        testOrder = orderJpaRepository.save(testOrder);

        // 5. 주문 아이템 생성 (쿠폰 없이)
        testOrderItem = new OrderItemJpaEntity(
                null,
                testOrder.getOrderId(),
                testProduct.getProductId(),
                testProductOption.getOptionId(),
                "테스트상품",
                50000L, // 가격
                5000L, // 할인 금액
                null, // 쿠폰 없음
                1 // 수량
        );
        testOrderItem = orderItemJpaRepository.save(testOrderItem);
    }

    @Test
    @DisplayName("PaymentFacade가 정상적으로 주입된다")
    void paymentFacade_isInjected() {
        // given & when & then
        assertThat(paymentFacade).isNotNull();
    }

    @Test
    @DisplayName("테스트 데이터가 정상적으로 생성된다")
    void testData_isCreated() {
        // given & when & then
        assertThat(testUser).isNotNull();
        assertThat(testProduct).isNotNull();
        assertThat(testProductOption).isNotNull();
        assertThat(testOrder).isNotNull();
        assertThat(testOrderItem).isNotNull();
        
        assertThat(testUser.getBalance()).isEqualTo(100000L);
        assertThat(testProduct.getProductName()).isEqualTo("테스트상품");
        assertThat(testProductOption.getPrice()).isEqualTo(50000L);
        assertThat(testOrder.getTotalAmount()).isEqualTo(50000L);
    }

    @Test
    @DisplayName("PaymentFacade의 의존성들이 정상적으로 주입된다")
    void paymentFacade_dependencies_areInjected() {
        // given & when & then
        assertThat(paymentFacade).isNotNull();
    }

    @Test
    @DisplayName("PaymentFacade가 정상적으로 결제 처리를 수행한다")
    void paymentFacade_processPayment_success() {
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
        // 결제 ID가 정상적으로 생성되었는지 확인
        assertThat(paymentId).isPositive();

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
    }
} 