package kr.hhplus.be.server.coupon.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.coupon.domain.type.CouponPolicyType;

import java.time.LocalDateTime;

public class CouponRequest {

    @Schema(description = "쿠폰 발급 요청")
    public record IssueCouponRequest(
            @Schema(description = "쿠폰 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            Long couponId,

            @Schema(description = "유저 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            Long userId,

            @Schema(description = "쿠폰 정책 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            Long policyId,

            @Schema(description = "할인 타입 (정액: FIXED / 비율: RATE)", requiredMode = Schema.RequiredMode.REQUIRED)
            CouponPolicyType typeSnapshot,

            @Schema(description = "할인율 (%)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
            Float discountRateSnapshot,

            @Schema(description = "할인 금액", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
            Long discountAmountSnapshot,

            @Schema(description = "최소 주문 금액", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
            Long minimumOrderAmountSnapshot,

            @Schema(description = "사용 기간", requiredMode = Schema.RequiredMode.REQUIRED)
            int usagePeriodSnapshot,

            @Schema(description = "만료일")
            LocalDateTime expiredAt
    ) {}
}