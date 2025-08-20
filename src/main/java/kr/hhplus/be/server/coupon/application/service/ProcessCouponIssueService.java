package kr.hhplus.be.server.coupon.application.service;

import kr.hhplus.be.server.common.exception.RestApiException;
import kr.hhplus.be.server.coupon.application.dto.ProcessResult;
import kr.hhplus.be.server.coupon.application.dto.SaveUserCouponCommand;
import kr.hhplus.be.server.coupon.application.usecase.ProcessCouponIssueUseCase;
import kr.hhplus.be.server.coupon.application.usecase.SaveUserCouponUseCase;
import kr.hhplus.be.server.coupon.domain.repository.CouponIssueCommandRepository;
import kr.hhplus.be.server.coupon.domain.repository.CouponIssueQueueRepository;
import kr.hhplus.be.server.coupon.domain.repository.CouponIssueRepository;
import kr.hhplus.be.server.coupon.exception.CouponErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 큐에서 1건 Pop → 실제 발급(save) → 결과 반환
 * - 비즈니스 로직은 기존 SaveUserCouponUseCase가 수행
 */
@Service
@RequiredArgsConstructor
public class ProcessCouponIssueService implements ProcessCouponIssueUseCase {

    private final CouponIssueQueueRepository queueRepository;
    private final CouponIssueCommandRepository commandRepository;
    private final SaveUserCouponUseCase saveUserCouponUseCase;

    @Transactional
    @Override
    public ProcessResult process(long couponId) {
        var popped = queueRepository.popNextSafe(couponId);

        switch (popped.type()) {
            case EMPTY -> {
                return ProcessResult.notFound(null);
            }
            case MISSING -> {
                // tombstone 처리된 케이스: 재시도 불필요, 모니터링 용도
                String member = popped.member();
                String rid = extractRid(member);
                return ProcessResult.notFound(rid);
            }
            case OK -> {
                String member = popped.member();
                String rid = extractRid(member);

                SaveUserCouponCommand cmd = commandRepository.find(rid);
                if (cmd == null) {
                    return ProcessResult.notFound(rid);
                }

                try {
                    saveUserCouponUseCase.save(cmd);
                    commandRepository.delete(rid);
                    return ProcessResult.success(rid);

                } catch (RestApiException e) {
                    if (e.getErrorCode() == CouponErrorCode.COUPON_REMAINING_EMPTY_ERROR) {
                        return ProcessResult.soldOut(rid);
                    }
                    return ProcessResult.fail(rid, "biz:" + e.getErrorCode().name());

                } catch (DataIntegrityViolationException e) {
                    commandRepository.delete(rid);
                    return ProcessResult.alreadyIssued(rid);

                } catch (Exception e) {
                    return ProcessResult.retry(rid, e.getClass().getSimpleName() + ":" + e.getMessage());
                }
            }
        }
        return ProcessResult.fail(null, "unreachable");
    }

    private String extractRid(String member) {
        int p = (member == null) ? -1 : member.lastIndexOf(':');
        return (p > 0) ? member.substring(0, p) : member;
    }
}
