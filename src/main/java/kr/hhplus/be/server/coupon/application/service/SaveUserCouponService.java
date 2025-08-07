package kr.hhplus.be.server.coupon.application.service;

import kr.hhplus.be.server.coupon.application.dto.SaveUserCouponCommand;
import kr.hhplus.be.server.coupon.application.usecase.SaveUserCouponUseCase;
import kr.hhplus.be.server.coupon.domain.model.CouponIssue;
import kr.hhplus.be.server.coupon.domain.model.UserCoupon;
import kr.hhplus.be.server.coupon.domain.repository.CouponIssueRepository;
import kr.hhplus.be.server.coupon.domain.repository.UserCouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * [UseCase 구현체]
 * SaveUserCouponUseCase 인터페이스를 구현한 클래스.
 *
 * 도메인 계층의 UserCouponRepository 사용하여 유저 쿠폰을 저장한다.
 *
 * 이 클래스는 오직 "유저 쿠폰 저장"라는 하나의 유스케이스만 책임지며,
 * 단일 책임 원칙(SRP)을 따르는 구조로 확장성과 테스트 용이성을 높인다.
 */
@Service
public class SaveUserCouponService implements SaveUserCouponUseCase {
    private final UserCouponRepository userCouponRepository;
    private final CouponIssueRepository couponIssueRepository;

    public SaveUserCouponService(
            UserCouponRepository userCouponRepository,
            CouponIssueRepository couponIssueRepository
    ) {
        this.userCouponRepository = userCouponRepository;
        this.couponIssueRepository = couponIssueRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void save(SaveUserCouponCommand command) {
        CouponIssue couponIssue = couponIssueRepository.findByIdForUpdate(command.couponId());

        CouponIssue updatedIssue = couponIssue.decreaseRemaining();

        couponIssueRepository.save(updatedIssue);

        UserCoupon userCoupon = UserCoupon.issueNew(
                command.userId(),
                command.couponId(),
                command.policyId(),
                command.typeSnapshot(),
                command.discountRateSnapshot(),
                command.usagePeriodSnapshot(),
                command.expiredAt()
        );

        userCouponRepository.insertOrUpdate(userCoupon);
    }
}
