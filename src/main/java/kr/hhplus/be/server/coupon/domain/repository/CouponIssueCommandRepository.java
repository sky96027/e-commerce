package kr.hhplus.be.server.coupon.domain.repository;

import kr.hhplus.be.server.coupon.application.dto.SaveUserCouponCommand;

public interface CouponIssueCommandRepository {
    void save(String reservationId, SaveUserCouponCommand cmd);
    SaveUserCouponCommand find(String reservationId);
    void delete(String reservationId);
}
