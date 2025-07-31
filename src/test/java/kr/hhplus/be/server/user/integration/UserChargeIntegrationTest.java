package kr.hhplus.be.server.user.integration;

import kr.hhplus.be.server.transactionhistory.domain.model.TransactionHistory;
import kr.hhplus.be.server.transactionhistory.domain.repository.TransactionHistoryRepository;
import kr.hhplus.be.server.transactionhistory.domain.type.TransactionType;
import kr.hhplus.be.server.user.application.dto.UserDto;
import kr.hhplus.be.server.user.application.facade.UserFacade;
import kr.hhplus.be.server.user.domain.model.User;
import kr.hhplus.be.server.user.domain.repository.UserRepository;
import kr.hhplus.be.server.user.infrastructure.entity.UserJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DisplayName("통합 테스트 - 잔고 충전 및 거래내역 저장")
public class UserChargeIntegrationTest {

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;

    @Test
    @DisplayName("잔고 충전 시 사용자 잔고가 변경되고, 거래 내역이 저장된다")
    void chargeWithHistory_success() {
        // given
        long chargeAmount = 10000L;
        System.out.println("테스트에서 삽입하는 balance: " + 5000L);
        User user = userRepository.insert(5000L);
        System.out.println("삽입된 유저 ID: " + user.getUserId());
        System.out.println("삽입된 유저 잔액: " + user.getBalance());
        long userId = user.getUserId();

        // when
        UserDto result = userFacade.chargeWithHistory(userId, chargeAmount);

        // then
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.balance()).isEqualTo(15000L);

        // 거래 내역 확인
        List<TransactionHistory> histories = transactionHistoryRepository.findAllByUserId(userId);
        assertThat(histories).hasSize(1);
        assertThat(histories.get(0).getUserId()).isEqualTo(userId);
        assertThat(histories.get(0).getType()).isEqualTo(TransactionType.CHARGE);
        assertThat(histories.get(0).getAmount()).isEqualTo(chargeAmount);
    }
}