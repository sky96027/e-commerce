package kr.hhplus.be.server.transactionhistory.application.service;

import kr.hhplus.be.server.transactionhistory.application.usecase.SaveTransactionUseCase;
import kr.hhplus.be.server.transactionhistory.domain.model.TransactionHistory;
import kr.hhplus.be.server.transactionhistory.infrastructure.entity.TransactionHistoryJpaEntity;
import kr.hhplus.be.server.transactionhistory.domain.repository.TransactionHistoryRepository;
import kr.hhplus.be.server.transactionhistory.domain.type.TransactionType;
import org.springframework.stereotype.Service;

/**
 * [UseCase 구현체]
 * FindHistoryUseCase 인터페이스를 구현한 클래스.
 *
 * 도메인 계층의 TransactionHistoryRepository 사용하여 거래 내역을 저장한다.
 *
 * 이 클래스는 오직 "거래 내역 저장"라는 하나의 유스케이스만 책임지며,
 * 단일 책임 원칙(SRP)을 따르는 구조로 확장성과 테스트 용이성을 높인다.
 */
@Service
public class SaveTransactionService implements SaveTransactionUseCase {

    private final TransactionHistoryRepository repository;

    public SaveTransactionService(TransactionHistoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(long userId, TransactionType type, long amount) {
        repository.save(userId, type, amount);
    }
}