package kr.hhplus.be.server.service.user;

import kr.hhplus.be.server.application.user.UserService;
import kr.hhplus.be.server.application.user.dto.TransactionHistoryDto;
import kr.hhplus.be.server.application.user.dto.UserDto;
import kr.hhplus.be.server.domain.transactionhistory.TransactionHistoryEntity;
import kr.hhplus.be.server.domain.transactionhistory.TransactionHistoryRepository;
import kr.hhplus.be.server.domain.transactionhistory.TransactionType;
import kr.hhplus.be.server.domain.user.UserEntity;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Application layer unit test - UserService")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionHistoryRepository transactionHistoryRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, transactionHistoryRepository);
    }

    @Test
    @DisplayName("유저 ID로 유저 조회 성공")
    void 유저_조회_성공() {
        // given
        long userId = 1L;
        UserEntity user = new UserEntity(userId, 5000L);
        when(userRepository.selectById(userId)).thenReturn(user);

        // when
        UserDto result = userService.findById(userId);

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
        UserEntity existing = new UserEntity(userId, 2000L);
        UserEntity updated = new UserEntity(userId, 5000L);

        when(userRepository.selectById(userId)).thenReturn(existing);
        when(userRepository.insertOrUpdate(eq(userId), eq(5000L))).thenReturn(updated);

        // when
        UserDto result = userService.chargeBalance(userId, chargeAmount);

        // then
        assertThat(result.balance()).isEqualTo(5000L);

        ArgumentCaptor<TransactionHistoryEntity> historyCaptor =
                ArgumentCaptor.forClass(TransactionHistoryEntity.class);
        verify(transactionHistoryRepository).save(historyCaptor.capture());

        TransactionHistoryEntity savedHistory = historyCaptor.getValue();
        assertThat(savedHistory.getUser().getUserId()).isEqualTo(userId);
        assertThat(savedHistory.getAmount()).isEqualTo(chargeAmount);
        assertThat(savedHistory.getType()).isEqualTo(TransactionType.CHARGE);
    }

    @Test
    @DisplayName("유저의 거래 내역을 조회할 수 있다")
    void 거래내역_조회_성공() {
        // given
        long userId = 1L;
        TransactionHistoryEntity history = new TransactionHistoryEntity(
                new UserEntity(userId, 5000L),
                TransactionType.CHARGE,
                5000L
        );

        when(transactionHistoryRepository.selectByUserId(userId))
                .thenReturn(List.of(history));

        // when
        List<TransactionHistoryDto> result = userService.getUserBalanceHistories(userId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).userId()).isEqualTo(userId);
        assertThat(result.get(0).type()).isEqualTo(TransactionType.CHARGE);
        assertThat(result.get(0).amount()).isEqualTo(5000L);
    }
}
