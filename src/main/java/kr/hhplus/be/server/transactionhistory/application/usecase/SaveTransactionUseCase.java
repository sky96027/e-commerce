package kr.hhplus.be.server.transactionhistory.application.usecase;

import kr.hhplus.be.server.transactionhistory.domain.TransactionType;

/**
 * [TransactionHistory 도메인 유스케이스]
 * 거래 내역을 기록하는 유스케이스 정의
 * 파라미터가 명확하고 같은 계층 간 공유이기에 Dto는 생략한다.
 */
public interface SaveTransactionUseCase {
    /**
     * 거래 내역을 기록한다.
     * @param userId         사용자 ID
     * @param amount         거래 금액
     * @param type           거래 유형 (CHARGE, USE 등)
     */
    void save(long userId, TransactionType type, long amount);
}