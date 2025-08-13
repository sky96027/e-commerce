package kr.hhplus.be.server.user.application.service.tx;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.transactionhistory.application.usecase.SaveTransactionUseCase;
import kr.hhplus.be.server.transactionhistory.domain.type.TransactionType;
import kr.hhplus.be.server.user.application.dto.UserDto;
import kr.hhplus.be.server.user.application.usecase.DeductUserBalanceUseCase;
import kr.hhplus.be.server.user.application.usecase.DeductUserBalanceWithHistoryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeductUserBalanceWithHistoryTxService implements DeductUserBalanceWithHistoryUseCase {

    private final DeductUserBalanceUseCase deductUserBalanceUseCase;
    private final SaveTransactionUseCase saveTransactionUseCase;

    @Override
    @Transactional
    public UserDto execute(long userId, long amount) {
        UserDto updated = deductUserBalanceUseCase.deduct(userId, amount);
        saveTransactionUseCase.save(userId, TransactionType.USE, amount);
        return updated;
    }
}
