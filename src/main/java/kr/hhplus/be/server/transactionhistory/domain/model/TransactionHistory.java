package kr.hhplus.be.server.transactionhistory.domain.model;

import kr.hhplus.be.server.transactionhistory.domain.type.TransactionType;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 거래 내역 도메인 모델
 */
@Getter
public class TransactionHistory {

    private final long transactionId;
    private final long userId;
    private final TransactionType type;
    private final LocalDateTime transactionTime;
    private final long amount;

    /**
     * 전체 필드를 초기화하는 생성자
     */
    public TransactionHistory(
            long transactionId,
            long userId,
            TransactionType type,
            LocalDateTime transactionTime,
            long amount
    ) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.type = type;
        this.transactionTime = transactionTime;
        this.amount = amount;
    }

}
