package kr.hhplus.be.server.domain.transactionhistory;

import java.util.List;

/**
 * 거래 내역 정보를 조회, 저장하기 위한 도메인 저장소 인터페이스
 * 구현체는 인프라 계층에 위치함
 */
public interface TransactionHistoryRepository {
    List<TransactionHistoryEntity> selectByUserId(long userId);
    TransactionHistoryEntity save(TransactionHistoryEntity transactionHistory);
}
