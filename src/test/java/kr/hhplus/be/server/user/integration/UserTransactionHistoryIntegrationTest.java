package kr.hhplus.be.server.user.integration;

import kr.hhplus.be.server.transactionhistory.application.dto.TransactionHistoryDto;
import kr.hhplus.be.server.transactionhistory.domain.model.TransactionHistory;
import kr.hhplus.be.server.transactionhistory.domain.repository.TransactionHistoryRepository;
import kr.hhplus.be.server.transactionhistory.domain.type.TransactionType;
import kr.hhplus.be.server.user.application.facade.UserFacade;
import kr.hhplus.be.server.user.domain.model.User;
import kr.hhplus.be.server.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DisplayName("통합 테스트 - 사용자 거래 내역 조회")
public class UserTransactionHistoryIntegrationTest {

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;

    @Test
    @DisplayName("사용자 거래 내역을 정상적으로 조회한다")
    void findUserHistories_success() {
        // given
        User user = userRepository.insert(10000L);
        long userId = user.getUserId();

        // 거래 내역 저장
        transactionHistoryRepository.save(userId, TransactionType.CHARGE, 5000L);
        transactionHistoryRepository.save(userId, TransactionType.USE, 2000L);

        // when
        List<TransactionHistoryDto> result = userFacade.findUserHistories(userId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).userId()).isEqualTo(userId);
        assertThat(result.get(1).userId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("거래 내역이 없는 사용자의 경우 빈 목록을 반환한다")
    void findUserHistories_empty() {
        // given
        User user = userRepository.insert(0L);
        long userId = user.getUserId();

        // when
        List<TransactionHistoryDto> result = userFacade.findUserHistories(userId);

        // then
        assertThat(result).isEmpty();
    }
}
