package kr.hhplus.be.server.user.application.facade;

import kr.hhplus.be.server.common.redis.RedisDistributedLockManager;
import kr.hhplus.be.server.transactionhistory.application.dto.TransactionHistoryDto;
import kr.hhplus.be.server.transactionhistory.application.usecase.FindHistoryUseCase;
import kr.hhplus.be.server.transactionhistory.application.usecase.SaveTransactionUseCase;
import kr.hhplus.be.server.user.application.dto.UserDto;
import kr.hhplus.be.server.user.application.usecase.ChargeUserBalanceUseCase;
import kr.hhplus.be.server.user.application.usecase.ChargeUserBalanceWithHistoryUseCase;
import kr.hhplus.be.server.user.application.usecase.FindUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

/**
 * [Facade]
 * - 역할: 분산락 + 유스케이스 오케스트레이션
 * - 트랜잭션은 내부 Tx 서비스에서 시작/종료
 */
@Component
@RequiredArgsConstructor
public class UserFacade {

    private final ChargeUserBalanceWithHistoryUseCase chargeUserBalanceWithHistoryUseCase;
    private final ChargeUserBalanceUseCase chargeUseCase;
    private final SaveTransactionUseCase saveTransactionUseCase;
    private final FindUserUseCase findUserUseCase;
    private final FindHistoryUseCase findHistoryUseCase;

    private final RedisDistributedLockManager lockManager;

    /**
     * 사용자 잔액을 충전하고, 그 내역을 거래 히스토리에 기록한다.
     * 트랜잭션으로 묶여 있어 둘 중 하나라도 실패 시 전체 롤백된다.
     *
     * @param userId 충전 대상 사용자 ID
     * @param amount 충전 금액
     * @return 충전 후 사용자 정보
     */
    public UserDto chargeWithHistory(long userId, long amount) {
        String key = "lock:user:charge:" + userId;

        String token = lockManager.lockBlocking(
                key, Duration.ofSeconds(3), Duration.ofSeconds(2), Duration.ofMillis(50));
        if (token == null) throw new IllegalStateException("잠시 후 다시 시도해 주세요.");

        try {
            return chargeUserBalanceWithHistoryUseCase.execute(userId, amount);
        } finally {
            lockManager.unlock(key, token); // 내부 트랜잭션 커밋 후 해제
        }
    }

    /**
     * 사용자 거래 히스토리를 불러온다.
     * 트랜잭션으로 묶여 있진 않지만, 도메인 간 결합을 피하기 위해 파사드에서 조합한다.
     * @param userId 조회 대상 사용자 ID
     * @return 대상 사용자의 거래 내역
     */
    public List<TransactionHistoryDto> findUserHistories(long userId) {
        findUserUseCase.findById(userId);

        return findHistoryUseCase.findAllByUserId(userId);
    }
}