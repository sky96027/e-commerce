package kr.hhplus.be.server.user.application.facade;

import kr.hhplus.be.server.common.redis.RedisDistributedLockManager;
import kr.hhplus.be.server.user.application.dto.UserDto;
import kr.hhplus.be.server.user.application.usecase.ChargeUserBalanceWithHistoryUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class UserFacadeTest {

    @Mock
    RedisDistributedLockManager lockManager;
    @Mock
    ChargeUserBalanceWithHistoryUseCase chargeUserBalanceWithHistoryUseCase; // ⬅️ Tx UC를 목
    @InjectMocks
    UserFacade userFacade;

    @Test
    @DisplayName("유저 잔고 충전 시 파사드는 락을 잡고 Tx 유스케이스에 위임한다")
    void chargeBalance_Facade_Success() {
        // given
        long userId = 1L;
        long amount = 3000L;
        String token = "t1";
        UserDto chargedUser = new UserDto(userId, 5000L);

        when(lockManager.lockBlocking(anyString(), any(), any(), any())).thenReturn(token);
        when(chargeUserBalanceWithHistoryUseCase.execute(userId, amount)).thenReturn(chargedUser);

        // when
        UserDto result = userFacade.chargeWithHistory(userId, amount);

        // then
        assertThat(result.balance()).isEqualTo(5000L);
        verify(lockManager).lockBlocking(eq("lock:user:charge:" + userId), any(), any(), any());
        verify(chargeUserBalanceWithHistoryUseCase).execute(userId, amount);
        verify(lockManager).unlock("lock:user:charge:" + userId, token);
        verifyNoMoreInteractions(chargeUserBalanceWithHistoryUseCase, lockManager);
    }
}
