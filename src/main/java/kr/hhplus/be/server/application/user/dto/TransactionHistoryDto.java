package kr.hhplus.be.server.application.user.dto;

import kr.hhplus.be.server.domain.transactionhistory.TransactionHistoryEntity;
import kr.hhplus.be.server.domain.transactionhistory.TransactionType;

import java.time.LocalDateTime;

public record TransactionHistoryDto(
        long transactionId,
        long userId,
        TransactionType type,
        long amount,
        LocalDateTime transactionTime
) {
    public static TransactionHistoryDto from(TransactionHistoryEntity entity) {
        return new TransactionHistoryDto(
                entity.getTransactionId(),
                entity.getUser().getUserId(),
                entity.getType(),
                entity.getAmount(),
                entity.getTransactionTime()
        );
    }
}