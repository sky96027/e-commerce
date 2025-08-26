package kr.hhplus.be.server.transactionhistory.application.service;

import kr.hhplus.be.server.common.cache.events.TransactionOccurredEvent;
import kr.hhplus.be.server.transactionhistory.application.usecase.SaveTransactionUseCase;
import kr.hhplus.be.server.transactionhistory.domain.repository.TransactionHistoryRepository;
import kr.hhplus.be.server.transactionhistory.domain.type.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * [UseCase 구현체]
 * SaveTransactionUseCase 인터페이스를 구현한 클래스.
 *
 * 도메인 계층의 TransactionHistoryRepository 사용하여 거래 내역을 저장한다.
 *
 * 이 클래스는 오직 "거래 내역 저장"라는 하나의 유스케이스만 책임지며,
 * 단일 책임 원칙(SRP)을 따르는 구조로 확장성과 테스트 용이성을 높인다.
 */
@Service
@RequiredArgsConstructor
public class SaveTransactionService implements SaveTransactionUseCase {

    private final TransactionHistoryRepository repository;
    private final ApplicationEventPublisher publisher;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void save(long userId, TransactionType type, long amount) {

        repository.save(userId, type, amount);
        publisher.publishEvent(new TransactionOccurredEvent(userId));
    }
}