package kr.hhplus.be.server.transactionhistory.application.dto;

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
        long transactionId,
        long userId,
        TransactionType type,
        long amount,
        LocalDateTime transactionTime
) {
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