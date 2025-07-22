package kr.hhplus.be.server.domain.transactionhistory;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.user.UserEntity;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 거래 내역 정보를 나타내는 JPA 엔티티 클래스
 * 상태 변경을 포함함
 */
@Getter
@Entity
public class TransactionHistoryEntity {
    public TransactionHistoryEntity() {}

    public TransactionHistoryEntity(UserEntity user, TransactionType type, long amount) {
        this.user = user;
        this.type = type;
        this.amount = amount;
        this.transactionTime = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private long transactionId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TransactionType type;

    @Column(name = "transaction_time", nullable = false)
    private LocalDateTime transactionTime;

    @Column(name = "amount", nullable = false)
    private long amount;
}
