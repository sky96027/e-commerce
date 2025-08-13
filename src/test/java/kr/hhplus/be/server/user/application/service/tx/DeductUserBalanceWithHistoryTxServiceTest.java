package kr.hhplus.be.server.user.application.service.tx;

import kr.hhplus.be.server.transactionhistory.application.usecase.SaveTransactionUseCase;
import kr.hhplus.be.server.transactionhistory.domain.type.TransactionType;
import kr.hhplus.be.server.user.application.dto.UserDto;
import kr.hhplus.be.server.user.application.usecase.ChargeUserBalanceUseCase;
import kr.hhplus.be.server.user.application.usecase.DeductUserBalanceUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class DeductUserBalanceWithHistoryTxServiceTest {

    @Mock
    DeductUserBalanceUseCase deductUserBalanceUseCase;

    @Mock
    SaveTransactionUseCase saveTransactionUseCase;

    @InjectMocks
    DeductUserBalanceWithHistoryTxService sut;

    @Test
    @DisplayName("사용자의 잔액을 차감하는 txService의 단위테스트 성공")
    void deduct_with_history() {
        // given
        long userId = 1L; long amount = 3000L;
        when(deductUserBalanceUseCase.deduct(userId, amount)).thenReturn(new UserDto(userId, 2000L));

        // when
        UserDto result = sut.execute(userId, amount);

        // then
        assertThat(result.balance()).isEqualTo(2000L);
        verify(deductUserBalanceUseCase).deduct(userId, amount);
        verify(saveTransactionUseCase).save(userId, TransactionType.USE, amount);
    }
}
