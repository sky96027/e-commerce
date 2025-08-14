package kr.hhplus.be.server.user.presentation.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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
        @JsonProperty("transactionId") Long transactionId,
        @Schema(description = "유저 ID", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("userId") Long userId,
        @Schema(description = "거래 타입", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("type") TransactionType type,
        @Schema(description = "거래 시간", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("transactionTime") LocalDateTime transactionTime,
        @Schema(description = "거래량", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("amount") Long amount
    ){
        @JsonCreator
        public GetUserTransactionHistories(
            @JsonProperty("transactionId") Long transactionId,
            @JsonProperty("userId") Long userId,
            @JsonProperty("type") TransactionType type,
            @JsonProperty("transactionTime") LocalDateTime transactionTime,
            @JsonProperty("amount") Long amount
        ) {
            this.transactionId = transactionId;
            this.userId = userId;
            this.type = type;
            this.transactionTime = transactionTime;
            this.amount = amount;
        }
    }
}
