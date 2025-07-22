package kr.hhplus.be.server.infrastructure.transactionhistory;

import kr.hhplus.be.server.domain.transactionhistory.TransactionHistoryEntity;
import kr.hhplus.be.server.domain.transactionhistory.TransactionHistoryRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class TransactionHistoryRepositoryImpl implements TransactionHistoryRepository {

    private final Map<Long, List<TransactionHistoryEntity>> historyTable = new HashMap<>();

    @Override
    public List<TransactionHistoryEntity> selectByUserId(long userId) {
        return historyTable.getOrDefault(userId, Collections.emptyList());
    }

    @Override
    public TransactionHistoryEntity save(TransactionHistoryEntity transactionHistory) {
        long userId = transactionHistory.getUser().getUserId();

        List<TransactionHistoryEntity> userHistories = historyTable
                .computeIfAbsent(userId, k -> new ArrayList<>());

        userHistories.add(transactionHistory);

        return transactionHistory;
    }
}