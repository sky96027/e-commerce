package kr.hhplus.be.server.coupon.application.service;

import kr.hhplus.be.server.common.exception.RestApiException;
import kr.hhplus.be.server.coupon.application.dto.SaveUserCouponCommand;
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

    private final CouponIssueQueueRepository queueRepository;
    private final CouponIssueCommandRepository commandRepository;

    @Override
    public String enqueue(SaveUserCouponCommand command) {
        String rid = UUID.randomUUID().toString();

        // 1) payload 저장
        commandRepository.save(rid, command);

        // 2) 큐 등록 (실패 시 payload 롤백)
        long score = queueRepository.enqueue(command.couponId(), command.userId(), rid);
        if (score < 0) {
            commandRepository.delete(rid);
            throw new RestApiException(CouponErrorCode.ENQUEUE_FAILED_ERROR);
        }
        return rid;
    }
}
