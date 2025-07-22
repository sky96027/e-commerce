package kr.hhplus.be.server.transactionhistory.application.service;

import kr.hhplus.be.server.transactionhistory.application.usecase.SaveTransactionUseCase;
import kr.hhplus.be.server.transactionhistory.domain.TransactionHistoryEntity;
import kr.hhplus.be.server.transactionhistory.domain.TransactionHistoryRepository;
import kr.hhplus.be.server.transactionhistory.domain.TransactionType;
import org.springframework.stereotype.Service;

/**
 * [UseCase 구현체]
 * 거래 내역을 생성하고 저장
 */
@Service
public class SaveTransactionService implements SaveTransactionUseCase {

    private final TransactionHistoryRepository repository;

    public SaveTransactionService(TransactionHistoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(long userId, TransactionType type, long amount) {
        TransactionHistoryEntity entity = new TransactionHistoryEntity(userId, type, amount);
        repository.save(entity);
    }
}