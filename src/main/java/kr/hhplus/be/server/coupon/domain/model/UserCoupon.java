package kr.hhplus.be.server.coupon.domain.model;

import kr.hhplus.be.server.coupon.domain.type.CouponPolicyType;
import kr.hhplus.be.server.coupon.domain.type.UserCouponStatus;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 유저 쿠폰 도메인 모델
 */
@Getter
public class UserCoupon {

    private final long userCouponId;
    private final long couponId;
    private final long userId;
    private final long policyId;
    private final UserCouponStatus status;

    private final CouponPolicyType typeSnapshot;
    private final Float discountRateSnapshot;
    private final int usagePeriodSnapshot;
    private final LocalDateTime expiredAt;

    public UserCoupon(
            long userCouponId,
            long couponId,
            long userId,
            long policyId,
            UserCouponStatus status,
            CouponPolicyType typeSnapshot,
            Float discountRateSnapshot,
            int usagePeriodSnapshot,
            LocalDateTime expiredAt
    ) {
        this.userCouponId = userCouponId;
        this.couponId = couponId;
        this.userId = userId;
        this.policyId = policyId;
        this.status = status;
        this.typeSnapshot = typeSnapshot;
        this.discountRateSnapshot = discountRateSnapshot;
        this.usagePeriodSnapshot = usagePeriodSnapshot;
        this.expiredAt = expiredAt;
    }

    public static UserCoupon issueNew(
            long userId,
            long couponId,
            long policyId,
            CouponPolicyType typeSnapshot,
            Float discountRateSnapshot,
            int usagePeriodSnapshot,
            LocalDateTime expiredAt
    ) {
        return new UserCoupon(
                0L,
                couponId,
                userId,
                policyId,
                UserCouponStatus.ISSUED,
                typeSnapshot,
                discountRateSnapshot,
                usagePeriodSnapshot,
                expiredAt
        );
    }
    public UserCoupon changeStatus(UserCouponStatus newStatus) {
        return new UserCoupon(
                this.userCouponId,
                this.couponId,
                this.userId,
                this.policyId,
                newStatus,
                this.typeSnapshot,
                this.discountRateSnapshot,
                this.usagePeriodSnapshot,
                this.expiredAt
        );
    }
}