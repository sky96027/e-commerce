package kr.hhplus.be.server.coupon.application.service;

import kr.hhplus.be.server.coupon.application.usecase.ChangeUserCouponStatusUseCase;
import kr.hhplus.be.server.coupon.domain.model.UserCoupon;
import kr.hhplus.be.server.coupon.domain.repository.UserCouponRepository;
import kr.hhplus.be.server.coupon.domain.type.UserCouponStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * [UseCase 구현체]
 * ChangeUserCouponStatusUseCase 인터페이스를 구현한 클래스.
 *
 * 도메인 계층의 UserCouponRepository를 사용하여 유저 쿠폰의 상태를 변경한다.
 *
 * 이 클래스는 오직 "유저의 쿠폰 상태 변경"라는 하나의 유스케이스만 책임지며,
 * 단일 책임 원칙(SRP)을 따르는 구조로 확장성과 테스트 용이성을 높인다.
 */
@Service
public class ChangeUserCouponStatusService implements ChangeUserCouponStatusUseCase {
    private final UserCouponRepository repository;

    public ChangeUserCouponStatusService(UserCouponRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public UserCoupon changeStatus(long userCouponId, UserCouponStatus newStatus) {
        UserCoupon current = repository.findByUserCouponId(userCouponId);
        UserCoupon updated = current.changeStatus(newStatus);
        repository.insertOrUpdate(updated);
        return updated;
    }
}
