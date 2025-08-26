package kr.hhplus.be.server.coupon.application.service;

import kr.hhplus.be.server.common.exception.RestApiException;
import kr.hhplus.be.server.common.redis.cache.events.UserCouponChangedEvent;
import kr.hhplus.be.server.coupon.application.dto.SaveUserCouponCommand;
import kr.hhplus.be.server.coupon.application.usecase.SaveUserCouponUseCase;
import kr.hhplus.be.server.coupon.domain.model.UserCoupon;
import kr.hhplus.be.server.coupon.domain.repository.CouponIssueRepository;
import kr.hhplus.be.server.coupon.domain.repository.UserCouponRepository;
import kr.hhplus.be.server.coupon.exception.CouponErrorCode;
import kr.hhplus.be.server.coupon.infrastructure.redis.CouponIssueCounter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
@RequiredArgsConstructor
public class SaveUserCouponService implements SaveUserCouponUseCase {
    private final UserCouponRepository userCouponRepository;
    private final CouponIssueRepository couponIssueRepository;

    private final ApplicationEventPublisher publisher;
    private final CouponIssueCounter counter;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void save(SaveUserCouponCommand command) {

        if (counter.getRemaining(command.couponId()) == -2L) {
            int dbRemain = couponIssueRepository.findRemainingById(command.couponId())
                    .orElseThrow(() -> new RestApiException(CouponErrorCode.COUPON_ISSUE_NOT_FOUND_ERROR));
            counter.init(command.couponId(), dbRemain);
        }

        // 1) Redis 원자 차감
        long after = counter.tryDecrement(command.couponId(), 1);
        if (after == -1L) {
            throw new RestApiException(CouponErrorCode.COUPON_REMAINING_EMPTY_ERROR);
        }
        if (after == -2L) { // 방어
            throw new RestApiException(CouponErrorCode.INVENTORY_NOT_INITIALIZED_ERROR);
        }

        boolean dbOk = false;
        try {
            // 2) DB write-through
            int rows = couponIssueRepository.decrementRemaining(command.couponId());
            if (rows != 1) throw new RestApiException(CouponErrorCode.COUPON_WRITE_THROUGH_FAILED_ERROR);
            dbOk = true;

            // 3) 유저 쿠폰 저장(유니크 인덱스로 중복 방지)
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

            publisher.publishEvent(new UserCouponChangedEvent(command.userId()));

        } catch (RuntimeException e) {
            // DB/애플리케이션 실패 시 보상(+1)
            counter.compensate(command.couponId(), 1);
            throw e;
        }
    }
}
