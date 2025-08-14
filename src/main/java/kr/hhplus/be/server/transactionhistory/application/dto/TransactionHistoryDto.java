package kr.hhplus.be.server.transactionhistory.application.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import kr.hhplus.be.server.transactionhistory.domain.model.TransactionHistory;
import kr.hhplus.be.server.transactionhistory.domain.type.TransactionType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 거래 내역을 담는 응답 DTO 클래스
 * Application Layer에서 사용되며,
 * 도메인 객체를 외부에 직접 노출하지 않도록 분리한 계층용 DTO
 */
public record TransactionHistoryDto(
        @JsonProperty("transactionId") long transactionId,
        @JsonProperty("userId") long userId,
        @JsonProperty("type") TransactionType type,
        @JsonProperty("amount") long amount,
        @JsonProperty("transactionTime") LocalDateTime transactionTime
) {
    /**
     * Jackson 역직렬화를 위한 생성자
     */
    @JsonCreator
    public TransactionHistoryDto(
            @JsonProperty("transactionId") long transactionId,
            @JsonProperty("userId") long userId,
            @JsonProperty("type") TransactionType type,
            @JsonProperty("amount") long amount,
            @JsonProperty("transactionTime") LocalDateTime transactionTime
    ) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.transactionTime = transactionTime;
    }

    /**
     * 도메인 모델로부터 DTO로 변환하는 정적 팩토리 메서드
     * @param transactionHistory 객체
     * @return TransactionHistoryDto 객체
     */
    public static TransactionHistoryDto from(TransactionHistory transactionHistory) {
        return new TransactionHistoryDto(
                transactionHistory.getTransactionId(),
                transactionHistory.getUserId(),
                transactionHistory.getType(),
                transactionHistory.getAmount(),
                transactionHistory.getTransactionTime()
        );
    }

    /**
     * 도메인 모델 리스트를 DTO 리스트로 변환
     * @param historyList 도메인 리스트
     * @return DTO 리스트
     */
    public static List<TransactionHistoryDto> fromList(List<TransactionHistory> historyList) {
        return historyList.stream()
                .map(TransactionHistoryDto::from)
                .collect(Collectors.toList());
    }
}