package kr.hhplus.be.server.coupon.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.coupon.domain.type.CouponPolicyType;
import kr.hhplus.be.server.coupon.domain.type.UserCouponStatus;

import java.time.LocalDateTime;

public class CouponResponse {

    @Schema(description = "유저가 보유한 쿠폰 정보")
    public record GetUserCoupon(
            @Schema(description = "유저 쿠폰 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            Long userCouponId,

            @Schema(description = "쿠폰 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            Long couponId,

            @Schema(description = "유저 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            Long userId,

            @Schema(description = "정책 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            Long policyId,

            @Schema(description = "쿠폰 상태", requiredMode = Schema.RequiredMode.REQUIRED)
            UserCouponStatus status,

            @Schema(description = "할인 타입 (정액: FIXED / 비율: RATE)", requiredMode = Schema.RequiredMode.REQUIRED)
            CouponPolicyType type,

            @Schema(description = "할인율 (%)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
            Float discountRateSnapshot,

            @Schema(description = "사용 기간", requiredMode = Schema.RequiredMode.REQUIRED)
            int usagePeriodSnapshot,

            @Schema(description = "만료일")
            LocalDateTime expiredAt
    ) {}
}