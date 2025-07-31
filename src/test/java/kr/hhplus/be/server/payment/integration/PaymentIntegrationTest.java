package kr.hhplus.be.server.payment.integration;

import kr.hhplus.be.server.payment.application.dto.PaymentDto;
import kr.hhplus.be.server.payment.application.dto.SavePaymentCommand;
import kr.hhplus.be.server.payment.application.service.FindByOrderIdService;
import kr.hhplus.be.server.payment.application.service.SavePaymentService;
import kr.hhplus.be.server.payment.application.usecase.FindByOrderIdUseCase;
import kr.hhplus.be.server.payment.application.usecase.SavePaymentUseCase;
import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.domain.repository.PaymentRepository;
import kr.hhplus.be.server.payment.domain.type.PaymentStatus;
import kr.hhplus.be.server.payment.infrastructure.entity.PaymentJpaEntity;
import kr.hhplus.be.server.payment.infrastructure.repository.PaymentJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@DisplayName("통합 테스트 - 결제")
public class PaymentIntegrationTest {

    @Autowired
    private SavePaymentUseCase savePaymentUseCase;

    @Autowired
    private FindByOrderIdUseCase findByOrderIdUseCase;

    @Autowired
    private SavePaymentService savePaymentService;

    @Autowired
    private FindByOrderIdService findByOrderIdService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentJpaRepository paymentJpaRepository;

    private long testOrderId;
    private long testUserId;
    private long testTotalAmount;
    private long testTotalDiscountAmount;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        testOrderId = 1L;
        testUserId = 100L;
        testTotalAmount = 50000L;
        testTotalDiscountAmount = 5000L;
    }

    @Test
    @DisplayName("결제를 정상적으로 저장한다")
    void savePayment_success() {
        // given
        SavePaymentCommand command = new SavePaymentCommand(
                testOrderId,
                testUserId,
                testTotalAmount,
                testTotalDiscountAmount,
                PaymentStatus.BEFORE_PAYMENT
        );

        // when
        long paymentId = savePaymentUseCase.save(command);

        // then
        assertThat(paymentId).isPositive();

        // 저장된 결제 정보 확인
        PaymentDto savedPayment = findByOrderIdUseCase.findByOrderId(testOrderId);
        assertThat(savedPayment).isNotNull();
        assertThat(savedPayment.paymentId()).isEqualTo(paymentId);
        assertThat(savedPayment.orderId()).isEqualTo(testOrderId);
        assertThat(savedPayment.userId()).isEqualTo(testUserId);
        assertThat(savedPayment.totalAmountSnapshot()).isEqualTo(testTotalAmount);
        assertThat(savedPayment.totalDiscountAmountSnapshot()).isEqualTo(testTotalDiscountAmount);
        assertThat(savedPayment.status()).isEqualTo(PaymentStatus.BEFORE_PAYMENT);
    }

    @Test
    @DisplayName("결제 완료 상태로 결제를 저장한다")
    void savePayment_withAfterPaymentStatus_success() {
        // given
        SavePaymentCommand command = new SavePaymentCommand(
                testOrderId,
                testUserId,
                testTotalAmount,
                testTotalDiscountAmount,
                PaymentStatus.AFTER_PAYMENT
        );

        // when
        long paymentId = savePaymentUseCase.save(command);

        // then
        assertThat(paymentId).isPositive();

        PaymentDto savedPayment = findByOrderIdUseCase.findByOrderId(testOrderId);
        assertThat(savedPayment).isNotNull();
        assertThat(savedPayment.status()).isEqualTo(PaymentStatus.AFTER_PAYMENT);
    }

    @Test
    @DisplayName("주문 ID로 결제 정보를 정상적으로 조회한다")
    void findByOrderId_success() {
        // given
        SavePaymentCommand command = new SavePaymentCommand(
                testOrderId,
                testUserId,
                testTotalAmount,
                testTotalDiscountAmount,
                PaymentStatus.BEFORE_PAYMENT
        );
        long paymentId = savePaymentUseCase.save(command);

        // when
        PaymentDto result = findByOrderIdUseCase.findByOrderId(testOrderId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.paymentId()).isEqualTo(paymentId);
        assertThat(result.orderId()).isEqualTo(testOrderId);
        assertThat(result.userId()).isEqualTo(testUserId);
        assertThat(result.totalAmountSnapshot()).isEqualTo(testTotalAmount);
        assertThat(result.totalDiscountAmountSnapshot()).isEqualTo(testTotalDiscountAmount);
        assertThat(result.status()).isEqualTo(PaymentStatus.BEFORE_PAYMENT);
    }

    @Test
    @DisplayName("존재하지 않는 주문 ID로 조회 시 예외 발생")
    void findByOrderId_notFound_throwsException() {
        // given
        long nonExistentOrderId = 999L;

        // when & then
        assertThatThrownBy(() -> findByOrderIdUseCase.findByOrderId(nonExistentOrderId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("결제를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("여러 결제를 저장하고 각각 조회한다")
    void saveMultiplePayments_andFindEach_success() {
        // given
        SavePaymentCommand command1 = new SavePaymentCommand(
                1L, 100L, 30000L, 3000L, PaymentStatus.BEFORE_PAYMENT
        );
        SavePaymentCommand command2 = new SavePaymentCommand(
                2L, 200L, 40000L, 4000L, PaymentStatus.AFTER_PAYMENT
        );

        // when
        long paymentId1 = savePaymentUseCase.save(command1);
        long paymentId2 = savePaymentUseCase.save(command2);

        // then
        PaymentDto result1 = findByOrderIdUseCase.findByOrderId(1L);
        PaymentDto result2 = findByOrderIdUseCase.findByOrderId(2L);

        assertThat(result1).isNotNull();
        assertThat(result1.paymentId()).isEqualTo(paymentId1);
        assertThat(result1.orderId()).isEqualTo(1L);
        assertThat(result1.userId()).isEqualTo(100L);
        assertThat(result1.totalAmountSnapshot()).isEqualTo(30000L);
        assertThat(result1.totalDiscountAmountSnapshot()).isEqualTo(3000L);
        assertThat(result1.status()).isEqualTo(PaymentStatus.BEFORE_PAYMENT);

        assertThat(result2).isNotNull();
        assertThat(result2.paymentId()).isEqualTo(paymentId2);
        assertThat(result2.orderId()).isEqualTo(2L);
        assertThat(result2.userId()).isEqualTo(200L);
        assertThat(result2.totalAmountSnapshot()).isEqualTo(40000L);
        assertThat(result2.totalDiscountAmountSnapshot()).isEqualTo(4000L);
        assertThat(result2.status()).isEqualTo(PaymentStatus.AFTER_PAYMENT);
    }

    @Test
    @DisplayName("할인 금액이 0인 결제를 저장한다")
    void savePayment_withZeroDiscount_success() {
        // given
        SavePaymentCommand command = new SavePaymentCommand(
                testOrderId,
                testUserId,
                testTotalAmount,
                0L, // 할인 금액 0
                PaymentStatus.BEFORE_PAYMENT
        );

        // when
        long paymentId = savePaymentUseCase.save(command);

        // then
        PaymentDto savedPayment = findByOrderIdUseCase.findByOrderId(testOrderId);
        assertThat(savedPayment).isNotNull();
        assertThat(savedPayment.totalDiscountAmountSnapshot()).isEqualTo(0L);
    }

    @Test
    @DisplayName("총 금액이 할인 금액보다 큰 결제를 저장한다")
    void savePayment_withValidAmounts_success() {
        // given
        long totalAmount = 100000L;
        long discountAmount = 20000L;
        
        SavePaymentCommand command = new SavePaymentCommand(
                testOrderId,
                testUserId,
                totalAmount,
                discountAmount,
                PaymentStatus.BEFORE_PAYMENT
        );

        // when
        long paymentId = savePaymentUseCase.save(command);

        // then
        PaymentDto savedPayment = findByOrderIdUseCase.findByOrderId(testOrderId);
        assertThat(savedPayment).isNotNull();
        assertThat(savedPayment.totalAmountSnapshot()).isEqualTo(totalAmount);
        assertThat(savedPayment.totalDiscountAmountSnapshot()).isEqualTo(discountAmount);
        assertThat(savedPayment.totalAmountSnapshot()).isGreaterThan(savedPayment.totalDiscountAmountSnapshot());
    }

    @Test
    @DisplayName("Service 클래스를 직접 사용하여 결제를 저장한다")
    void savePayment_usingServiceDirectly_success() {
        // given
        SavePaymentCommand command = new SavePaymentCommand(
                testOrderId,
                testUserId,
                testTotalAmount,
                testTotalDiscountAmount,
                PaymentStatus.BEFORE_PAYMENT
        );

        // when
        long paymentId = savePaymentService.save(command);

        // then
        assertThat(paymentId).isPositive();

        PaymentDto savedPayment = findByOrderIdService.findByOrderId(testOrderId);
        assertThat(savedPayment).isNotNull();
        assertThat(savedPayment.paymentId()).isEqualTo(paymentId);
    }

    @Test
    @DisplayName("Service 클래스를 직접 사용하여 결제를 조회한다")
    void findByOrderId_usingServiceDirectly_success() {
        // given
        SavePaymentCommand command = new SavePaymentCommand(
                testOrderId,
                testUserId,
                testTotalAmount,
                testTotalDiscountAmount,
                PaymentStatus.BEFORE_PAYMENT
        );
        long paymentId = savePaymentService.save(command);

        // when
        PaymentDto result = findByOrderIdService.findByOrderId(testOrderId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.paymentId()).isEqualTo(paymentId);
        assertThat(result.orderId()).isEqualTo(testOrderId);
    }
} 