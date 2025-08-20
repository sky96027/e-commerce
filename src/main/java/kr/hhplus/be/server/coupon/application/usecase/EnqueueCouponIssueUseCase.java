package kr.hhplus.be.server.coupon.application.usecase;

import kr.hhplus.be.server.coupon.application.dto.SaveUserCouponCommand;

public interface EnqueueCouponIssueUseCase {
    String enqueue(SaveUserCouponCommand cmd);
}