package kr.hhplus.be.server.transactionhistory.infrastructure.repository;

import kr.hhplus.be.server.transactionhistory.domain.model.TransactionHistory;
import kr.hhplus.be.server.transactionhistory.domain.repository.TransactionHistoryRepository;
import kr.hhplus.be.server.transactionhistory.domain.type.TransactionType;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
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
        throttle(200);
        return historyTable.getOrDefault(userId, Collections.emptyList());
    }

    @Override
    public void save(long userId, TransactionType type, long amount) {
        throttle(200);
        long newId = idGenerator.getAndIncrement();
        LocalDateTime now = LocalDateTime.now();

        TransactionHistory transaction = new TransactionHistory(
                newId, userId, type, now, amount
        );

        historyTable.computeIfAbsent(userId, k -> new ArrayList<>());
    }

    private void throttle(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep((long) (Math.random() * millis));
        } catch (InterruptedException ignored) {

        }
    }
}