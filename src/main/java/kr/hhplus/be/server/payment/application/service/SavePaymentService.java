package kr.hhplus.be.server.payment.application.service;

import kr.hhplus.be.server.payment.application.dto.SavePaymentCommand;
import kr.hhplus.be.server.payment.application.usecase.SavePaymentUseCase;
import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.domain.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

/**
 * [UseCase 구현체]
 * SavePaymentUseCase 인터페이스를 구현한 클래스.
 *
 * 도메인 계층의 PaymentRepository 사용하여 결제 정보를 저장한다.
 *
 * 이 클래스는 오직 "결제 정보 저장"라는 하나의 유스케이스만 책임지며,
 * 단일 책임 원칙(SRP)을 따르는 구조로 확장성과 테스트 용이성을 높인다.
 */
@Service
public class SavePaymentService implements SavePaymentUseCase {
    private final PaymentRepository repository;

    public SavePaymentService(PaymentRepository repository) { this.repository = repository; }

    @Override
    public long save(SavePaymentCommand command) {
        Payment payment = new Payment(
                0L,
                command.orderId(),
                command.userId(),
                command.totalAmount(),
                command.totalDiscountAmount(),
                command.status()
        );

        Payment savedPayment = repository.save(payment);
        return savedPayment.getPaymentId();
    }
}
