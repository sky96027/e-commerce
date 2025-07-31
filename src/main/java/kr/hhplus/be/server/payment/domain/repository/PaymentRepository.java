package kr.hhplus.be.server.payment.domain.repository;

import kr.hhplus.be.server.payment.domain.model.Payment;

/**
 * 결제를 처리하기 위한 도메인 저장소 인터페이스
 * 구현체는 인프라 계층에 위치함
 */
public interface PaymentRepository {
    Payment save(Payment payment);
    Payment findById(long orderId);
}
