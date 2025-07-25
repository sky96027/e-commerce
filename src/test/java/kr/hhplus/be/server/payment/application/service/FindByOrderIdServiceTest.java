package kr.hhplus.be.server.payment.application.service;

import kr.hhplus.be.server.payment.application.dto.PaymentDto;
import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.domain.repository.PaymentRepository;
import kr.hhplus.be.server.payment.domain.type.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class FindByOrderIdServiceTest {
    @Mock
    private PaymentRepository paymentRepository;
    @InjectMocks
    private FindByOrderIdService findByOrderIdService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        findByOrderIdService = new FindByOrderIdService(paymentRepository);
    }

    @Test
    @DisplayName("주문 ID로 결제 정보 정상 조회")
    void findByOrderId_success() {
        // given
        long orderId = 1L;
        Payment payment = new Payment(10L, orderId, 2L, 10000L, 2000L, PaymentStatus.BEFORE_PAYMENT);
        when(paymentRepository.findByOrderId(orderId)).thenReturn(payment);

        // when
        PaymentDto result = findByOrderIdService.findByOrderId(orderId);

        // then
        assertThat(result.orderId()).isEqualTo(orderId);
        assertThat(result.paymentId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("존재하지 않는 주문 ID로 결제 정보 조회 시 NPE 발생")
    void findByOrderId_notFound_npe() {
        // given
        long orderId = 2L;
        when(paymentRepository.findByOrderId(orderId)).thenReturn(null);

        // when
        // PaymentDto.from(null)에서 NPE 발생 예상
        try {
            findByOrderIdService.findByOrderId(orderId);
            assertThat(false).isTrue(); // 실패해야 정상
        } catch (NullPointerException e) {
            assertThat(true).isTrue(); // NPE 발생이 정상
        }
    }
} 