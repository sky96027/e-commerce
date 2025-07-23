package kr.hhplus.be.server.transactionhistory.application.service;

import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.transactionhistory.application.dto.TransactionHistoryDto;
import kr.hhplus.be.server.transactionhistory.application.usecase.FindHistoryUseCase;
import kr.hhplus.be.server.transactionhistory.domain.model.TransactionHistory;
import kr.hhplus.be.server.transactionhistory.domain.repository.TransactionHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * [UseCase 구현체]
 * FindHistoryUseCase 인터페이스를 구현한 클래스.
 *
 * 도메인 계층의 TransactionHistoryRepository 사용하여 거래 내역 데이터를 조회하고,
 * 그 결과를 TransactionHistoryDto로 변환하여 반환한다.
 *
 * 이 클래스는 오직 "거래 내역 조회"라는 하나의 유스케이스만 책임지며,
 * 단일 책임 원칙(SRP)을 따르는 구조로 확장성과 테스트 용이성을 높인다.
 */
@Service
public class FindHistoryService implements FindHistoryUseCase {
    private final TransactionHistoryRepository repository;

    public FindHistoryService(TransactionHistoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<TransactionHistoryDto> findByUserId(long userId) {
        List<TransactionHistory> history = repository.selectByUserId(userId);
        return TransactionHistoryDto.fromList(history);
    }
}
