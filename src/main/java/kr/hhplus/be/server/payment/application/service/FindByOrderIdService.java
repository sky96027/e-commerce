package kr.hhplus.be.server.payment.application.service;

import kr.hhplus.be.server.payment.application.dto.PaymentDto;
import kr.hhplus.be.server.payment.application.usecase.FindByOrderIdUseCase;
import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.domain.repository.PaymentRepository;
import org.springframework.stereotype.Service;

/**
 * [UseCase 구현체]
 * FindOrderByOrderIdUseCase 인터페이스를 구현한 클래스.
 *
 * 도메인 계층의 OrderRepository를 사용하여 주문 데이터를 조회하고,
 * 그 결과를 OrderDto로 변환하여 반환한다.
 *
 * 이 클래스는 오직 "주문 조회"라는 하나의 유스케이스만 책임지며,
 * 단일 책임 원칙(SRP)을 따르는 구조로 확장성과 테스트 용이성을 높인다.
 */
@Service
public class FindByOrderIdService implements FindByOrderIdUseCase {

    private final PaymentRepository paymentRepository;

    public FindByOrderIdService(
            PaymentRepository paymentRepository
    ) {
        this.paymentRepository = paymentRepository;
    }

    /**
     * 주어진 주문 ID를 기반으로 결제 정보를 조회하고, DTO로 변환하여 반환한다.
     * @param orderId 조회할 결제의 주문 ID
     * @return 결제 정보를 담은 PaymentDto
     */
    @Override
    public PaymentDto findByOrderId(long orderId) {
        Payment payment = paymentRepository.findById(orderId);
        if (payment == null) {
            throw new IllegalArgumentException("결제를 찾을 수 없습니다. orderId: " + orderId);
        }
        return PaymentDto.from(payment);
    }
}
