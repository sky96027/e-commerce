package kr.hhplus.be.server.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class PaymentRequest {

    @Schema(description = "결제 요청")
    public record CreatePaymentRequest(
            @Schema(description = "주문 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            Long orderId,

            @Schema(description = "유저 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            Long userId
    ) {}

}
