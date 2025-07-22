package kr.hhplus.be.server.transactionhistory.domain;

import jakarta.persistence.*;
import kr.hhplus.be.server.user.domain.UserEntity;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 거래 내역 정보를 나타내는 JPA 엔티티 클래스
 * 상태 변경을 포함함
 */
@Getter
@Entity
public class TransactionHistoryEntity {

    protected TransactionHistoryEntity() {}

    public TransactionHistoryEntity(long userId, TransactionType type, long amount) {
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
