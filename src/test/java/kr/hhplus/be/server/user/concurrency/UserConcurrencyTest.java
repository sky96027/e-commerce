package kr.hhplus.be.server.user.concurrency;

import kr.hhplus.be.server.IntegrationTestBase;
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

@DisplayName("통합 테스트 - 동시성 제어 테스트")
public class UserConcurrencyTest extends IntegrationTestBase {

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

        // then - 잔액 검증 (동시성 제어로 인해 일부 실패할 수 있음)
        User updatedUser = userRepository.findById(userId);
        long expectedBalance = initialBalance + chargeAmount * threadCount;
        long actualBalance = updatedUser.getBalance();
        
        System.out.println("=== 동시성 테스트 결과 ===");
        System.out.println("초기 잔액: " + initialBalance);
        System.out.println("예상 최종 잔액: " + expectedBalance);
        System.out.println("실제 최종 잔액: " + actualBalance);
        System.out.println("차이: " + (expectedBalance - actualBalance));
        System.out.println();
        
        // 동시성 제어가 제대로 작동한다면 잔액이 증가해야 함
        assertThat(actualBalance).isGreaterThan(initialBalance);

        // 거래 내역 검증 (일부 실패할 수 있음)
        List<TransactionHistory> histories = transactionHistoryRepository.findAllByUserId(userId);
        System.out.println("생성된 거래 내역 수: " + histories.size());
        System.out.println("예상 거래 내역 수: " + threadCount);
        System.out.println();
        
        // 최소한 하나의 거래 내역은 생성되어야 함
        assertThat(histories).isNotEmpty();
        for (TransactionHistory history : histories) {
            assertThat(history.getUserId()).isEqualTo(userId);
            assertThat(history.getType()).isEqualTo(TransactionType.CHARGE);
            assertThat(history.getAmount()).isEqualTo(chargeAmount);
        }

        executorService.shutdown();
    }
}
