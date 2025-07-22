package kr.hhplus.be.server.controller.coupon.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class CouponRequest {

    @Schema(description = "쿠폰 발급 요청")
    public record IssueCouponRequest(
            @Schema(description = "유저 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            Long userId,

            @Schema(description = "쿠폰 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            Long couponId
    ) {}
}