package kr.hhplus.be.server.transactionhistory.application.service;

import kr.hhplus.be.server.transactionhistory.application.dto.TransactionHistoryDto;
import kr.hhplus.be.server.transactionhistory.application.usecase.FindHistoryUseCase;
import kr.hhplus.be.server.transactionhistory.domain.TransactionHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * [UseCase 구현체]
 * 거래 내역을 조회
 */
@Service
public class FindHistoryService implements FindHistoryUseCase {
    private final TransactionHistoryRepository repository;

    public FindHistoryService(TransactionHistoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<TransactionHistoryDto> findByUserId(long userId) {
        repository.selectByUserId(userId);
        return null;
    }
}
