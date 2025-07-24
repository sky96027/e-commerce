package kr.hhplus.be.server.coupon.infrastructure.repository;

import kr.hhplus.be.server.coupon.domain.model.UserCoupon;
import kr.hhplus.be.server.coupon.domain.repository.UserCouponRepository;
import kr.hhplus.be.server.coupon.domain.type.UserCouponStatus;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory UserCouponRepository 구현체
 */
@Repository
public class UserCouponRepositoryImpl implements UserCouponRepository {

    private final Map<Long, UserCoupon> table = new HashMap<>();
    private final AtomicLong sequence = new AtomicLong(1); // ID 시퀀스

    /**
     * 특정 유저의 보유 쿠폰 목록 조회
     */
    @Override
    public List<UserCoupon> selectCouponsByUserId(long userId) {
        throttle(200);
        return table.values().stream()
                .filter(coupon -> coupon.getUserId() == userId)
                .toList();
    }

    /**
     * userCouponId로 단일 쿠폰 조회
     */
    @Override
    public Optional<UserCoupon> selectByUserCouponId(long userCouponId) {
        throttle(200);
        return Optional.ofNullable(table.get(userCouponId));
    }

    /**
     * 유저의 쿠폰을 저장
     */
    @Override
    public void insertOrUpdate(UserCoupon userCoupon) {
        throttle(200);
        long id = sequence.getAndIncrement();

        UserCoupon withId = new UserCoupon(
                id,
                userCoupon.getCouponId(),
                userCoupon.getUserId(),
                userCoupon.getPolicyId(),
                UserCouponStatus.ISSUED,
                userCoupon.getTypeSnapshot(),
                userCoupon.getDiscountRateSnapshot(),
                userCoupon.getDiscountAmountSnapshot(),
                userCoupon.getMinimumOrderAmountSnapshot(),
                userCoupon.getUsagePeriodSnapshot(),
                userCoupon.getExpiredAt()
        );

        table.put(id, withId);
    }

    private void throttle(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep((long) (Math.random() * millis));
        } catch (InterruptedException ignored) {
        }
    }
}