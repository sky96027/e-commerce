package kr.hhplus.be.server.coupon.concurrency;

import kr.hhplus.be.server.coupon.application.facade.CouponFacade;
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
    CouponFacade couponFacade;

    @Autowired
    private CouponIssueRepository couponIssueRepository;

    @Test
    @DisplayName("50개의 동시 요청에도 쿠폰 발급이 정확히 처리된다")
    void saveUserCoupon_concurrency_success() throws InterruptedException {
        // given
        int initialRemaining = 1000;
        int threadCount = 50;

        CouponIssue issue = new CouponIssue(
                null,
                /*policyId*/ 1L,
                /*totalIssued*/ 1000,
                /*remaining*/ initialRemaining,
                LocalDateTime.now(),
                CouponIssueStatus.ISSUABLE,
                10.0f, /*usagePeriod*/ 30, CouponPolicyType.RATE
        );
        CouponIssue saved = couponIssueRepository.save(issue);
        long couponIssueId = saved.getCouponIssueId();

        ExecutorService pool = Executors.newFixedThreadPool(10);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            pool.execute(() -> {
                try {
                    start.await();
                    SaveUserCouponCommand cmd = new SaveUserCouponCommand(
                            /*userId*/ 1L,
                            /*couponId(=couponIssueId)*/ couponIssueId,
                            /*policyId*/ 1L,
                            CouponPolicyType.RATE, 10.0f, 30,
                            LocalDateTime.now().plusDays(30)
                    );
                    couponFacade.issueToUser(cmd); // ⬅️ Facade 통해 호출(락 경유)
                } catch (Exception ignored) {
                } finally {
                    done.countDown();
                }
            });
        }

        start.countDown();
        done.await();
        pool.shutdown();

        // then
        CouponIssue updated = couponIssueRepository.findById(couponIssueId);
        assertThat(updated.getRemaining()).isEqualTo(initialRemaining - threadCount);
    }
} 