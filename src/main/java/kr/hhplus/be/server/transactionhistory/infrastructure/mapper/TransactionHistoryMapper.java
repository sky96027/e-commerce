package kr.hhplus.be.server.transactionhistory.infrastructure.mapper;

import kr.hhplus.be.server.transactionhistory.domain.model.TransactionHistory;
import kr.hhplus.be.server.transactionhistory.infrastructure.entity.TransactionHistoryJpaEntity;
import org.springframework.stereotype.Component;

/**
 * TransactionHistoryJpaEntity ↔ TransactionHistory 변환 매퍼
 */
@Component
public class TransactionHistoryMapper {
    public TransactionHistory toDomain(TransactionHistoryJpaEntity entity) {
        return new TransactionHistory(
                entity.getTransactionId(),
                entity.getUserId(),
                entity.getType(),
                entity.getTransactionTime(),
                entity.getAmount()
        );
    }

    public TransactionHistoryJpaEntity toEntity(TransactionHistory domain) {
        return new TransactionHistoryJpaEntity(
                domain.getUserId(),
                domain.getType(),
                domain.getAmount(),
                domain.getTransactionTime()
        );
    }
}
