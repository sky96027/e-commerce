package kr.hhplus.be.server.coupon.application.service;

import kr.hhplus.be.server.coupon.application.dto.SaveUserCouponCommand;
import kr.hhplus.be.server.coupon.application.usecase.SaveUserCouponUseCase;
import kr.hhplus.be.server.coupon.domain.model.UserCoupon;
import kr.hhplus.be.server.coupon.domain.repository.UserCouponRepository;
import kr.hhplus.be.server.transactionhistory.domain.repository.TransactionHistoryRepository;
import org.springframework.stereotype.Service;

/**
 * [UseCase 구현체]
 * SaveUserCouponUseCase 인터페이스를 구현한 클래스.
 *
 * 도메인 계층의 UserCouponRepository 사용하여 거래 내역을 저장한다.
 *
 * 이 클래스는 오직 "유저 쿠폰 저장"라는 하나의 유스케이스만 책임지며,
 * 단일 책임 원칙(SRP)을 따르는 구조로 확장성과 테스트 용이성을 높인다.
 */
@Service
public class SaveUserCouponService implements SaveUserCouponUseCase {
    private final UserCouponRepository repository;

    public SaveUserCouponService(UserCouponRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(SaveUserCouponCommand command) {
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
        repository.save(userCoupon);
    }
}
