package kr.hhplus.be.server.user.application;

import kr.hhplus.be.server.transactionhistory.application.dto.TransactionHistoryDto;
import kr.hhplus.be.server.transactionhistory.application.usecase.FindHistoryUseCase;
import kr.hhplus.be.server.transactionhistory.application.usecase.SaveTransactionUseCase;
import kr.hhplus.be.server.transactionhistory.domain.type.TransactionType;
import kr.hhplus.be.server.user.application.dto.UserDto;
import kr.hhplus.be.server.user.application.facade.UserFacade;
import kr.hhplus.be.server.user.application.service.FindUserService;
import kr.hhplus.be.server.user.application.usecase.ChargeUserBalanceUseCase;
import kr.hhplus.be.server.user.application.usecase.FindUserUseCase;
import kr.hhplus.be.server.user.domain.model.User;
import kr.hhplus.be.server.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Application layer unit test - UserService")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ChargeUserBalanceUseCase chargeUseCase;
    @Mock
    private SaveTransactionUseCase saveTransactionUseCase;
    @Mock
    private FindHistoryUseCase findHistoryUseCase;
    @Mock
    private FindUserUseCase findUserUseCase;


    private FindUserService findUserService;
    private UserFacade userFacade;

    @BeforeEach
    void setUp() {
        userFacade = new UserFacade(
                chargeUseCase,
                saveTransactionUseCase,
                findUserUseCase,
                findHistoryUseCase
        );

        findUserService = new FindUserService(userRepository);
    }

    @Test
    @DisplayName("유저 ID로 유저 조회 성공")
    void 유저_조회_성공() {
        // given
        long userId = 1L;
        User user = new User(userId, 5000L);
        when(userRepository.selectById(userId)).thenReturn(user);

        // when
        UserDto result = findUserService.findById(userId);

        // then
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.balance()).isEqualTo(5000L);
    }

    @Test
    @DisplayName("유저 잔고 충전 시 거래 내역이 저장된다")
    void 잔고_충전_및_거래내역_저장_성공() {
        // given
        long userId = 1L;
        long chargeAmount = 3000L;
        UserDto chargedUser = new UserDto(userId, 5000L);

        when(chargeUseCase.charge(userId, chargeAmount)).thenReturn(chargedUser);

        // when
        UserDto result = userFacade.chargeWithHistory(userId, chargeAmount);

        // then
        assertThat(result.balance()).isEqualTo(5000L);
        verify(saveTransactionUseCase).save(userId, TransactionType.CHARGE, chargeAmount);
    }

    @Test
    @DisplayName("유저의 거래 내역을 조회할 수 있다")
    void 거래내역_조회_성공() {
        // given
        long transactionId = 1L;
        long userId = 1L;
        TransactionHistoryDto dto = new TransactionHistoryDto(transactionId, userId, TransactionType.CHARGE, 5000L, null);
        when(findHistoryUseCase.findByUserId(userId)).thenReturn(List.of(dto));

        // when
        List<TransactionHistoryDto> result = userFacade.findUserHistories(userId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).userId()).isEqualTo(userId);
        assertThat(result.get(0).type()).isEqualTo(TransactionType.CHARGE);
        assertThat(result.get(0).amount()).isEqualTo(5000L);
    }
}
