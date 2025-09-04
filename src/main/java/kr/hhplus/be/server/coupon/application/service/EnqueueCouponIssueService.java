package kr.hhplus.be.server.coupon.application.service;

import kr.hhplus.be.server.common.exception.RestApiException;
import kr.hhplus.be.server.coupon.application.dto.SaveUserCouponCommand;
import kr.hhplus.be.server.coupon.application.event.dto.CouponIssueRequestedEvent;
import kr.hhplus.be.server.coupon.application.kafka.producer.CouponEventProducer;
import kr.hhplus.be.server.coupon.application.usecase.EnqueueCouponIssueUseCase;
import kr.hhplus.be.server.coupon.domain.repository.CouponIssueCommandRepository;
import kr.hhplus.be.server.coupon.domain.repository.CouponIssueQueueRepository;
import kr.hhplus.be.server.coupon.domain.repository.CouponIssueRepository;
import kr.hhplus.be.server.coupon.exception.CouponErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 발급 요청 예약: reservationId 생성 → 커맨드 저장 → 큐에 enq
 */
@Service
@RequiredArgsConstructor
public class EnqueueCouponIssueService implements EnqueueCouponIssueUseCase {

    private final CouponEventProducer producer;

    @Override
    public String enqueue(SaveUserCouponCommand command) {
        String rid = UUID.randomUUID().toString();

        CouponIssueRequestedEvent event = new CouponIssueRequestedEvent(
                rid,
                command.userId(),
                command.couponId(),
                command.policyId(),
                command.typeSnapshot(),
                command.discountRateSnapshot(),
                command.usagePeriodSnapshot(),
                command.expiredAt()
        );

        producer.send(event);
        return rid;
    }
}
