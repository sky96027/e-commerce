package kr.hhplus.be.server.coupon.domain.model;

import kr.hhplus.be.server.coupon.domain.type.CouponPolicyType;
import kr.hhplus.be.server.coupon.domain.type.UserCouponStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserCouponTest {
    @Test
    @DisplayName("유저 쿠폰 발급(issueNew) 정상 동작")
    void issueNew_success() {
        // given
        long userId = 1L;
        long couponId = 2L;
        long policyId = 3L;
        CouponPolicyType type = CouponPolicyType.FIXED;
        Float discountRate = 10.0f;
        Long discountAmount = 1000L;
        Long minOrderAmount = 5000L;
        int usagePeriod = 30;
        LocalDateTime expiredAt = LocalDateTime.now().plusDays(30);

        // when
        UserCoupon userCoupon = UserCoupon.issueNew(userId, couponId, policyId, type, discountRate, usagePeriod, expiredAt);

        // then
        assertThat(userCoupon.getUserCouponId()).isNull();  // null을 기대
        assertThat(userCoupon.getUserId()).isEqualTo(userId);
        assertThat(userCoupon.getCouponId()).isEqualTo(couponId);
        assertThat(userCoupon.getPolicyId()).isEqualTo(policyId);
        assertThat(userCoupon.getTypeSnapshot()).isEqualTo(type);
        assertThat(userCoupon.getDiscountRateSnapshot()).isEqualTo(discountRate);
        assertThat(userCoupon.getUsagePeriodSnapshot()).isEqualTo(usagePeriod);
        assertThat(userCoupon.getExpiredAt()).isEqualTo(expiredAt);
        assertThat(userCoupon.getStatus()).isEqualTo(UserCouponStatus.ISSUED);
    }

    @Test
    @DisplayName("유저 쿠폰 상태 변경(changeStatus) 정상 동작")
    void changeStatus_success() {
        // given
        UserCoupon original = new UserCoupon(
                10L, 2L, 1L, 3L, UserCouponStatus.ISSUED,
                CouponPolicyType.FIXED, 10.0f, 30, LocalDateTime.now().plusDays(30)
        );
        // when
        UserCoupon updated = original.changeStatus(UserCouponStatus.USED);
        // then
        assertThat(updated.getStatus()).isEqualTo(UserCouponStatus.USED);
        assertThat(updated.getUserCouponId()).isEqualTo(original.getUserCouponId());
        assertThat(updated.getCouponId()).isEqualTo(original.getCouponId());
        assertThat(updated.getUserId()).isEqualTo(original.getUserId());
        assertThat(updated.getPolicyId()).isEqualTo(original.getPolicyId());
    }
} 