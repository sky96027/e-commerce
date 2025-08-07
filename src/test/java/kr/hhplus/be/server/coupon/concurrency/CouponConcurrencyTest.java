package kr.hhplus.be.server.coupon.concurrency;

import kr.hhplus.be.server.coupon.application.service.SaveUserCouponService;
import kr.hhplus.be.server.coupon.application.dto.SaveUserCouponCommand;
import kr.hhplus.be.server.coupon.domain.model.CouponIssue;
import kr.hhplus.be.server.coupon.domain.repository.CouponIssueRepository;
import kr.hhplus.be.server.coupon.domain.type.CouponIssueStatus;
import kr.hhplus.be.server.coupon.domain.type.CouponPolicyType;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("통합 테스트 - 쿠폰 발급 동시성 제어 테스트")
public class CouponConcurrencyTest {

    @Autowired
    private SaveUserCouponService saveUserCouponService;

    @Autowired
    private CouponIssueRepository couponIssueRepository;

    @Test
    @DisplayName("50개의 동시 요청에도 쿠폰 발급이 정확히 처리된다")
    void saveUserCoupon_concurrency_success() throws InterruptedException {
        // given
        int initialRemaining = 1000;
        int threadCount = 50;
        long userId = 1L;
        long couponId = 1L;
        long policyId = 1L;

        // 쿠폰 이슈 생성
        CouponIssue couponIssue = new CouponIssue(
            null,
            policyId,
            1000, // totalIssued
            initialRemaining,
            LocalDateTime.now(),
            CouponIssueStatus.ISSUABLE,
            10.0f, // discountRateSnapshot
            30, // usagePeriodSnapshot
            CouponPolicyType.RATE
        );
        CouponIssue updateddCouponIssue = couponIssueRepository.save(couponIssue);

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when - 동시에 같은 쿠폰 발급 요청
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            executorService.execute(() -> {
                try {
                    System.out.println("Thread " + threadIndex + " 시작");
                    SaveUserCouponCommand command = new SaveUserCouponCommand(
                        userId, 
                        couponId, 
                        policyId, 
                        CouponPolicyType.RATE,
                        10.0f, 
                        30, 
                        LocalDateTime.now().plusDays(30)
                    );
                    saveUserCouponService.save(command);
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

        // then - 남은 수량 검증
        CouponIssue updatedCouponIssue = couponIssueRepository.findById(couponId);
        assertThat(updatedCouponIssue.getRemaining()).isEqualTo(initialRemaining - threadCount);

        executorService.shutdown();
    }
} 