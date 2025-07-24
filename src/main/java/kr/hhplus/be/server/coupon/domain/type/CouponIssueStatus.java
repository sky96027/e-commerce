package kr.hhplus.be.server.coupon.domain.type;

/**
 * 쿠폰 발급 상태를 나타내는 enum
 */
public enum CouponIssueStatus {
    NOT_STARTED, // 발급 시작 전
    ISSUABLE,    // 발급 가능
    EXHAUSTED,   // 수량 소진
    EXPIRED      // 기간 만료
}