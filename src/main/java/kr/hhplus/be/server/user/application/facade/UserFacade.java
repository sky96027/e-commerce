package kr.hhplus.be.server.user.application.facade;

import kr.hhplus.be.server.transactionhistory.application.dto.TransactionHistoryDto;
import kr.hhplus.be.server.transactionhistory.application.usecase.FindHistoryUseCase;
import kr.hhplus.be.server.transactionhistory.application.usecase.SaveTransactionUseCase;
import kr.hhplus.be.server.transactionhistory.domain.type.TransactionType;
import kr.hhplus.be.server.user.application.dto.UserDto;
import kr.hhplus.be.server.user.application.usecase.ChargeUserBalanceUseCase;
import kr.hhplus.be.server.user.application.usecase.FindUserUseCase;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * [Facade]
 * 사용자 관련 복합 유스케이스를 조합하는 파사드 클래스.
 * 도메인 간 경계를 명확히 하기 위해 다른 도메인은 항상 facade를 거친다.
 */
@Component
public class UserFacade {

    private final ChargeUserBalanceUseCase chargeUseCase;
    private final SaveTransactionUseCase saveTransactionUseCase;
    private final FindUserUseCase findUserUseCase;
    private final FindHistoryUseCase findHistoryUseCase;

    public UserFacade(
            ChargeUserBalanceUseCase chargeUseCase,
            SaveTransactionUseCase saveTransactionUseCase,
            FindUserUseCase findUserUseCase,
            FindHistoryUseCase findHistoryUseCase
    ) {
        this.chargeUseCase = chargeUseCase;
        this.saveTransactionUseCase = saveTransactionUseCase;
        this.findUserUseCase = findUserUseCase;
        this.findHistoryUseCase = findHistoryUseCase;
    }

    /**
     * 사용자 잔액을 충전하고, 그 내역을 거래 히스토리에 기록한다.
     * 트랜잭션으로 묶여 있어 둘 중 하나라도 실패 시 전체 롤백된다.
     *
     * @param userId 충전 대상 사용자 ID
     * @param amount 충전 금액
     * @return 충전 후 사용자 정보
     */
    @Transactional
    public UserDto chargeWithHistory(long userId, long amount) {
        UserDto updated = chargeUseCase.charge(userId, amount);
        saveTransactionUseCase.save(userId, TransactionType.CHARGE, amount);
        return updated;
    }

    /**
     * 사용자 거래 히스토리를 불러온다.
     * 트랜잭션으로 묶여 있진 않지만, 도메인 간 결합을 피하기 위해 파사드에서 조합한다.
     * @param userId 조회 대상 사용자 ID
     * @return 대상 사용자의 거래 내역
     */
    public List<TransactionHistoryDto> findUserHistories(long userId) {
        findUserUseCase.findById(userId);

        return findHistoryUseCase.findByUserId(userId);
    }
}