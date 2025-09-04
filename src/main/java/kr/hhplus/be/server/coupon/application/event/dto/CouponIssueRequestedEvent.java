package kr.hhplus.be.server.coupon.application.event.dto;

import kr.hhplus.be.server.coupon.domain.type.CouponPolicyType;

import java.time.LocalDateTime;

/**
 * Kafka 발행용 이벤트 DTO
 * - 쿠폰 발급 요청 이벤트
 */
public record CouponIssueRequestedEvent(
        String reservationId,            // 예약 ID
        long userId,                     // 사용자 ID
        long couponId,                   // 쿠폰 ID
        long policyId,                   // 정책 ID
        CouponPolicyType typeSnapshot,   // 정책 타입 스냅샷
        Float discountRateSnapshot,      // 할인율 스냅샷
        int usagePeriodSnapshot,         // 사용 기간 스냅샷
        LocalDateTime expiredAt          // 만료일 스냅샷
) {}