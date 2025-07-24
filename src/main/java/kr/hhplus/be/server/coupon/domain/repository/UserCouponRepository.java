package kr.hhplus.be.server.coupon.domain.repository;

import kr.hhplus.be.server.coupon.domain.model.UserCoupon;

import java.util.List;
import java.util.Optional;

/**
 * 유저가 소유한 쿠폰의 조회, 저장하기 위한 도메인 저장소 인터페이스
 * 구현체는 인프라 계층에 위치함
 */
public interface UserCouponRepository {
    List<UserCoupon> selectCouponsByUserId(long userId);

    Optional<UserCoupon> selectByUserCouponId(long userCouponId);

    void insertOrUpdate(UserCoupon userCoupon);
}
