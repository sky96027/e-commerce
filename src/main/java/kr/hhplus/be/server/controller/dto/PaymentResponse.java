package kr.hhplus.be.server.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class PaymentResponse {

    @Schema(description = "결제 응답")
    public record GetPaymentResponse(
            @Schema(description = "결제 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            Long paymentId,

            @Schema(description = "주문 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            Long orderId,

            @Schema(description = "유저 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            Long userId,

            @Schema(description = "총 결제 금액 스냅샷", requiredMode = Schema.RequiredMode.REQUIRED)
            Long totalAmountSnapshot,

            @Schema(description = "총 할인 금액 스냅샷", requiredMode = Schema.RequiredMode.REQUIRED)
            Long totalDiscountAmountSnapshot,

            @Schema(description = "결제 상태", requiredMode = Schema.RequiredMode.REQUIRED)
            String status
    ) {}
}
