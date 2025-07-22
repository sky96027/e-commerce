package kr.hhplus.be.server.transactionhistory.application.dto;

import kr.hhplus.be.server.transactionhistory.domain.TransactionHistoryEntity;
import kr.hhplus.be.server.transactionhistory.domain.TransactionType;

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
                entity.getUserId(),
                entity.getType(),
                entity.getAmount(),
                entity.getTransactionTime()
        );
    }
}