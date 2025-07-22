package kr.hhplus.be.server.transactionhistory.application.usecase;

import kr.hhplus.be.server.transactionhistory.application.dto.TransactionHistoryDto;

import java.util.List;

/**
 * [TransactionHistory 도메인 유스케이스]
 * 거래 내역을 기록하는 유스케이스 정의
 * 파라미터가 명확하고 같은 계층 간 공유이기에 Dto는 생략한다.
 */
public interface FindHistoryUseCase {
    /**
     * 거래 내역을 조회한다.
     *
     * @param userId 사용자 ID
     * @return
     */
    List<TransactionHistoryDto> findByUserId(long userId);
}
