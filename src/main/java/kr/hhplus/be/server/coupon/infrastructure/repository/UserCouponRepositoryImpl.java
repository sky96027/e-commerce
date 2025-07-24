package kr.hhplus.be.server.coupon.infrastructure.repository;

import kr.hhplus.be.server.coupon.domain.model.UserCoupon;
import kr.hhplus.be.server.coupon.domain.repository.UserCouponRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory UserCouponRepository 구현체
 */
@Repository
public class UserCouponRepositoryImpl implements UserCouponRepository {

    // userId → 보유한 쿠폰 리스트
    private final Map<Long, List<UserCoupon>> table = new HashMap<>();
    private final AtomicLong sequence = new AtomicLong(1); // ID 시퀀스

    /**
     * 특정 유저의 보유 쿠폰 목록 조회
     */
    @Override
    public List<UserCoupon> selectCouponsByUserId(long userId) {
        throttle(200);
        return table.getOrDefault(userId, Collections.emptyList());
    }

    /**
     * 유저의 쿠폰을 추가 저장
     */
    @Override
    public void save(UserCoupon userCoupon) {
        throttle(200);
        long id = sequence.getAndIncrement();

        UserCoupon withId = new UserCoupon(
                id,
                userCoupon.getCouponId(),
                userCoupon.getUserId(),
                userCoupon.getPolicyId(),
                userCoupon.getStatus(),
                userCoupon.getUsagePeriodSnapshot(),
                userCoupon.getExpiredAt()
        );

        table.computeIfAbsent(withId.getUserId(), __ -> new ArrayList<>())
                .add(withId);
    }
    /*@Override
    public void save(UserCoupon userCoupon) {
        throttle(200);
        table.computeIfAbsent(userCoupon.getUserId(), id -> new ArrayList<>())
                .add(userCoupon);
    }*/

    private void throttle(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep((long) (Math.random() * millis));
        } catch (InterruptedException ignored) {
        }
    }
}