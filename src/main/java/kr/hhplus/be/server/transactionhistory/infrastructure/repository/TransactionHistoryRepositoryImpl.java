package kr.hhplus.be.server.transactionhistory.infrastructure.repository;

import kr.hhplus.be.server.transactionhistory.domain.model.TransactionHistory;
import kr.hhplus.be.server.transactionhistory.domain.repository.TransactionHistoryRepository;
import kr.hhplus.be.server.transactionhistory.domain.type.TransactionType;
import kr.hhplus.be.server.transactionhistory.infrastructure.entity.TransactionHistoryJpaEntity;
import kr.hhplus.be.server.transactionhistory.infrastructure.mapper.TransactionHistoryMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * In-memory TransactionHistoryRepository 구현체
 */
@Repository
public class TransactionHistoryRepositoryImpl implements TransactionHistoryRepository {

    private final TransactionHistoryJpaRepository jpaRepository;
    private final TransactionHistoryMapper mapper;

    public TransactionHistoryRepositoryImpl(TransactionHistoryJpaRepository jpaRepository, TransactionHistoryMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<TransactionHistory> findAllByUserId(long userId) {
        return jpaRepository.findAllByUserId(userId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(long userId, TransactionType type, long amount) {
        TransactionHistory domain = new TransactionHistory(userId, type, amount); // 도메인 규칙 포함
        TransactionHistoryJpaEntity entity = mapper.toEntity(domain);
        jpaRepository.save(entity);
    }
}