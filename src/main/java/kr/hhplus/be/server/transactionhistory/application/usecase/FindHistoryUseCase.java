package kr.hhplus.be.server.transactionhistory.application.usecase;

import kr.hhplus.be.server.transactionhistory.application.dto.TransactionHistoryDto;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * [UseCase - Port In]
 * 거래 내역을 조회하는 유스케이스에 대한 추상 정의.
 *
 * 이 인터페이스는 애플리케이션 계층에서 유저 ID를 기반으로
 * 거래 내역을 조회하는 기능을 외부(presentation, facade 등)에 노출하기 위한 계약(Contract)이다.
 *
 * 구현체는 service 패키지 내에서 정의되며,
 * presentation 계층은 이 인터페이스만 의존함으로써 구현체에 대한 결합을 피할 수 있다.
 */
public interface FindHistoryUseCase {
    /**
     * 거래 내역을 조회한다.
     * @param userId 사용자 ID
     * @return 거래 내역 리스트
     */
    List<TransactionHistoryDto> findAllByUserId(long userId);
}
