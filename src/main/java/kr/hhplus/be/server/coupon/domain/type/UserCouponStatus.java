package kr.hhplus.be.server.coupon.domain.type;

/**
 * 사용자 쿠폰 상태를 나타내는 enum
 */
public enum UserCouponStatus {
    ISSUED,     // 발급됨
    USED,       // 사용됨
    EXPIRED     // 만료됨
}
