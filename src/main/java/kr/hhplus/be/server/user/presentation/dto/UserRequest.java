package kr.hhplus.be.server.user.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class UserRequest {
    public record ChargeBalance (
        @Schema(description = "유저 ID", requiredMode = Schema.RequiredMode.REQUIRED)
        Long userId,
        @Schema(description = "충전량", requiredMode = Schema.RequiredMode.REQUIRED)
        Long amount
    ){}
}
