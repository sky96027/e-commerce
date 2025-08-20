package kr.hhplus.be.server.coupon.application.usecase;

import kr.hhplus.be.server.coupon.application.dto.ProcessResult;

public interface ProcessCouponIssueUseCase {
    // 큐에서 꺼낸 reservationId 1건 처리 (원자 예약→DB 커밋→이벤트)
    ProcessResult process(long couponId);
}
