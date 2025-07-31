package kr.hhplus.be.server.transactionhistory.infrastructure.repository;

import kr.hhplus.be.server.transactionhistory.infrastructure.entity.TransactionHistoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionHistoryJpaRepository extends JpaRepository<TransactionHistoryJpaEntity, Long> {
    List<TransactionHistoryJpaEntity> findAllByUserId(long userId);
}
