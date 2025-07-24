package kr.hhplus.be.server.transactionhistory.infrastructure.mapper;

import kr.hhplus.be.server.transactionhistory.domain.model.TransactionHistory;
import kr.hhplus.be.server.transactionhistory.infrastructure.entity.TransactionHistoryJpaEntity;

/**
 * TransactionHistoryJpaEntity ↔ TransactionHistory 변환 매퍼
 */
public class TransactionHistoryMapper {
    private TransactionHistory toDomain(TransactionHistoryJpaEntity entity) {
        return new TransactionHistory(
                entity.getTransactionId(),
                entity.getUserId(),
                entity.getType(),
                entity.getTransactionTime(),
                entity.getAmount()
        );
    }

    private TransactionHistoryJpaEntity toEntity(TransactionHistory domain) {
        return new TransactionHistoryJpaEntity(
                domain.getUserId(),
                domain.getType(),
                domain.getAmount()
        );
    }
}
