package kr.hhplus.be.server.user.concurrency;

import kr.hhplus.be.server.transactionhistory.domain.model.TransactionHistory;
import kr.hhplus.be.server.transactionhistory.domain.repository.TransactionHistoryRepository;
import kr.hhplus.be.server.transactionhistory.domain.type.TransactionType;
import kr.hhplus.be.server.user.application.facade.UserFacade;
import kr.hhplus.be.server.user.domain.model.User;
import kr.hhplus.be.server.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("통합 테스트 - 동시성 제어 테스트")
public class UserConcurrencyTest {

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;

    @Test
    @DisplayName("50개의 동시 요청에도 사용자 잔고와 거래 내역이 정확히 처리된다")
    void chargeWithHistory_concurrency_success() throws InterruptedException {
        // given
        long initialBalance = 5000L;
        int threadCount = 50;
        long chargeAmount = 1000L;

        User user = userRepository.insert(initialBalance);
        long userId = user.getUserId();

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when - 동시에 같은 사용자에게 충전
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            executorService.execute(() -> {
                try {
                    System.out.println("Thread " + threadIndex + " 시작");
                    userFacade.chargeWithHistory(userId, chargeAmount);
                    System.out.println("Thread " + threadIndex + " 성공");
                } catch (Exception e) {
                    System.err.println("Thread " + threadIndex + " 실패: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then - 잔액 검증
        User updatedUser = userRepository.findById(userId);
        assertThat(updatedUser.getBalance()).isEqualTo(initialBalance + chargeAmount * threadCount);

        // 거래 내역 검증
        List<TransactionHistory> histories = transactionHistoryRepository.findAllByUserId(userId);
        assertThat(histories).hasSize(threadCount);
        for (TransactionHistory history : histories) {
            assertThat(history.getUserId()).isEqualTo(userId);
            assertThat(history.getType()).isEqualTo(TransactionType.CHARGE);
            assertThat(history.getAmount()).isEqualTo(chargeAmount);
        }

        executorService.shutdown();
    }
}
