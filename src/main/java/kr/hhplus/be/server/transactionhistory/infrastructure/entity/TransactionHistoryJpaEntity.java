package kr.hhplus.be.server.transactionhistory.infrastructure.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.transactionhistory.domain.type.TransactionType;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 거래 내역 정보를 나타내는 JPA 엔티티 클래스
 */
@Getter
@Entity
@Table(name = "TRANSACTION_HISTORY")
public class TransactionHistoryJpaEntity {

    protected TransactionHistoryJpaEntity() {}

    public TransactionHistoryJpaEntity(long userId, TransactionType type, long amount) {
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.transactionTime = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private long transactionId;

    @Column(name = "user_id", nullable = false)
    private long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TransactionType type;

    @Column(name = "transaction_time", nullable = false)
    private LocalDateTime transactionTime;

    @Column(name = "amount", nullable = false)
    private long amount;
}
