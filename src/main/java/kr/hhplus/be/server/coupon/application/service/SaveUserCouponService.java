package kr.hhplus.be.server.coupon.application.service;

import kr.hhplus.be.server.coupon.application.dto.SaveUserCouponCommand;
import kr.hhplus.be.server.coupon.application.usecase.SaveUserCouponUseCase;
import kr.hhplus.be.server.coupon.domain.model.CouponIssue;
import kr.hhplus.be.server.coupon.domain.model.UserCoupon;
import kr.hhplus.be.server.coupon.domain.repository.CouponIssueRepository;
import kr.hhplus.be.server.coupon.domain.repository.UserCouponRepository;
import org.springframework.stereotype.Service;

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
    private final ConcurrentHashMap<Long, ReentrantLock> lockMap = new ConcurrentHashMap<>();// 추가 필요

    public SaveUserCouponService(
            UserCouponRepository userCouponRepository,
            CouponIssueRepository couponIssueRepository
    ) {
        this.userCouponRepository = userCouponRepository;
        this.couponIssueRepository = couponIssueRepository;
    }

    @Override
    public void save(SaveUserCouponCommand command) {
        ReentrantLock lock = lockMap.computeIfAbsent(command.couponId(), k -> new ReentrantLock());
        lock.lock();
        try {
            // 1. 발급 정보 조회
            CouponIssue couponIssue = couponIssueRepository.selectById(command.couponId());

            // 2. 남은 수량 감소 (0 이하일 경우 예외 발생)
            CouponIssue updatedIssue = couponIssue.decreaseRemaining();

            // 3. 유저 쿠폰 발급 객체 생성
            UserCoupon userCoupon = UserCoupon.issueNew(
                    command.userId(),
                    command.couponId(),
                    command.policyId(),
                    command.typeSnapshot(),
                    command.discountRateSnapshot(),
                    command.discountAmountSnapshot(),
                    command.minimumOrderAmountSnapshot(),
                    command.usagePeriodSnapshot(),
                    command.expiredAt()
            );

            // 4. 저장
            couponIssueRepository.update(updatedIssue);
            userCouponRepository.insertOrUpdate(userCoupon);

        } finally {
            lock.unlock();
        }
    }
}
