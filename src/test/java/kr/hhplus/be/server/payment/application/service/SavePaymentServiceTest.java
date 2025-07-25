package kr.hhplus.be.server.payment.application.service;

import kr.hhplus.be.server.payment.application.dto.SavePaymentCommand;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class SavePaymentServiceTest {
    @Mock
    private PaymentRepository paymentRepository;
    @InjectMocks
    private SavePaymentService savePaymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        savePaymentService = new SavePaymentService(paymentRepository);
    }

    @Test
    @DisplayName("결제 정보 정상 저장")
    void save_success() {
        // given
        SavePaymentCommand command = new SavePaymentCommand(1L, 2L, 10000L, 2000L, PaymentStatus.BEFORE_PAYMENT);
        doNothing().when(paymentRepository).save(any(Payment.class));

        // when
        long paymentId = savePaymentService.save(command);

        // then
        assertThat(paymentId).isGreaterThan(0L);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }
} 