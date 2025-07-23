package kr.hhplus.be.server.transactionhistory.infrastructure.repository;

import kr.hhplus.be.server.transactionhistory.domain.model.TransactionHistory;
import kr.hhplus.be.server.transactionhistory.domain.type.TransactionType;
import kr.hhplus.be.server.transactionhistory.infrastructure.entity.TransactionHistoryJpaEntity;
import kr.hhplus.be.server.transactionhistory.domain.repository.TransactionHistoryRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory TransactionHistoryRepository 구현체
 */
@Repository
public class TransactionHistoryRepositoryImpl implements TransactionHistoryRepository {

    private final Map<Long, List<TransactionHistory>> historyTable = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public List<TransactionHistory> selectByUserId(long userId) {
        return historyTable.getOrDefault(userId, Collections.emptyList());
    }

    @Override
    public TransactionHistory save(long userId, TransactionType type, long amount) {
        long newId = idGenerator.getAndIncrement();
        LocalDateTime now = LocalDateTime.now();

        TransactionHistory transaction = new TransactionHistory(
                newId, userId, type, now, amount
        );

        List<TransactionHistory> userHistories = historyTable
                .computeIfAbsent(userId, k -> new ArrayList<>());

        userHistories.add(transaction);

        return transaction;
    }
}