package kr.hhplus.be.server.user.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.transactionhistory.domain.type.TransactionType;

import java.time.LocalDateTime;

public class UserResponse {
    public record FindById(
        @Schema(description = "유저 ID", requiredMode = Schema.RequiredMode.REQUIRED)
        Long userId,
        @Schema(description = "잔액", requiredMode = Schema.RequiredMode.REQUIRED)
        Long balance
    ){}

    public record GetUserTransactionHistories(
        @Schema(description = "거래 ID", requiredMode = Schema.RequiredMode.REQUIRED)
        Long transactionId,
        @Schema(description = "유저 ID", requiredMode = Schema.RequiredMode.REQUIRED)
        Long userId,
        @Schema(description = "거래 타입", requiredMode = Schema.RequiredMode.REQUIRED)
        TransactionType type,
        @Schema(description = "거래 시간", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime transactionTime,
        @Schema(description = "거래량", requiredMode = Schema.RequiredMode.REQUIRED)
        Long amount
    ){}
}
