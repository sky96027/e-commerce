package kr.hhplus.be.server.user.application.service.tx;

import kr.hhplus.be.server.transactionhistory.application.usecase.SaveTransactionUseCase;
import kr.hhplus.be.server.transactionhistory.domain.type.TransactionType;
import kr.hhplus.be.server.user.application.dto.UserDto;
import kr.hhplus.be.server.user.application.usecase.ChargeUserBalanceUseCase;
import kr.hhplus.be.server.user.application.usecase.ChargeUserBalanceWithHistoryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChargeUserBalanceWithHistoryTxService implements ChargeUserBalanceWithHistoryUseCase {

    private final ChargeUserBalanceUseCase chargeUserBalanceUseCase;
    private final SaveTransactionUseCase saveTransactionUseCase;      // 타 도메인 UseCase

    @Override
    @Transactional
    public UserDto execute(long userId, long amount) {
        UserDto updated = chargeUserBalanceUseCase.charge(userId, amount);
        saveTransactionUseCase.save(userId, TransactionType.CHARGE, amount);
        return updated;
    }
}
