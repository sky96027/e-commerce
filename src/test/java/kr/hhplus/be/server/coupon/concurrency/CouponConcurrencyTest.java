package kr.hhplus.be.server.coupon.concurrency;

import kr.hhplus.be.server.IntegrationTestBase;
import kr.hhplus.be.server.coupon.application.dto.ProcessResult;
import kr.hhplus.be.server.coupon.application.facade.CouponFacade;
import kr.hhplus.be.server.coupon.application.service.SaveUserCouponService;
import kr.hhplus.be.server.coupon.application.dto.SaveUserCouponCommand;
import kr.hhplus.be.server.coupon.application.usecase.EnqueueCouponIssueUseCase;
import kr.hhplus.be.server.coupon.application.usecase.ProcessCouponIssueUseCase;
import kr.hhplus.be.server.coupon.application.usecase.SaveUserCouponUseCase;
import kr.hhplus.be.server.coupon.domain.model.CouponIssue;
import kr.hhplus.be.server.coupon.domain.repository.CouponIssueRepository;
import kr.hhplus.be.server.coupon.domain.type.CouponIssueStatus;
import kr.hhplus.be.server.coupon.domain.type.CouponPolicyType;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        // 워커가 테스트 중 자동 실행되지 않도록 비활성화
        "coupon.issue.worker.enabled=false",
        // 혹시 켜져 있어도 큐 대상이 없게 막기
        "coupon.issue.worker.coupon-ids="
})
@DisplayName("통합 테스트 - 쿠폰 발급 동시성 제어 테스트")
public class CouponConcurrencyTest extends IntegrationTestBase {

    @Autowired
    private SaveUserCouponUseCase saveUserCouponUseCase;

    @Autowired
    private EnqueueCouponIssueUseCase enqueueCouponIssueUseCase;

    @Autowired
    private ProcessCouponIssueUseCase processCouponIssueUseCase;

    @Autowired
    private CouponIssueRepository couponIssueRepository;

    /*@Test
    @DisplayName("50개의 동시 요청에도 쿠폰 발급이 정확히 처리된다(동기 처리 사용)")
    void saveUserCoupon_concurrency_success() throws InterruptedException {
        // given
        int initialRemaining = 1000;
        int threadCount = 50;

        AtomicInteger success = new AtomicInteger();
        List<Throwable> errors = Collections.synchronizedList(new ArrayList<>());

        CouponIssue issue = new CouponIssue(
                null,
                *//*policyId*//* 1L,
                *//*totalIssued*//* 1000,
                *//*remaining*//* initialRemaining,
                LocalDateTime.now(),
                CouponIssueStatus.ISSUABLE,
                10.0f, *//*usagePeriod*//* 30, CouponPolicyType.RATE
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
                            *//*userId*//* 1L,
                            *//*couponId(=couponIssueId)*//* couponIssueId,
                            *//*policyId*//* 1L,
                            CouponPolicyType.RATE, 10.0f, 30,
                            LocalDateTime.now().plusDays(30)
                    );
                    saveUserCouponUseCase.save(cmd);
                    success.incrementAndGet();
                } catch (Throwable t) {
                    errors.add(t);
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
    }*/


    @Test
    @DisplayName("50개의 동시 예약 후 drain하면 정확히 발급 처리된다(queue, drain 사용)")
    void enqueue_then_drain_success() throws Exception {
        // given
        int initialRemaining = 1000;
        int threadCount = 50;

        CouponIssue issue = new CouponIssue(
                null, /*policyId*/ 1L,
                /*totalIssued*/ 1000,
                /*remaining*/ initialRemaining,
                LocalDateTime.now(),
                CouponIssueStatus.ISSUABLE,
                10.0f, /*usagePeriod*/ 30,
                CouponPolicyType.RATE
        );
        long couponIssueId = couponIssueRepository.save(issue).getCouponIssueId();

        ExecutorService pool = Executors.newFixedThreadPool(10);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done  = new CountDownLatch(threadCount);
        List<Throwable> errors = Collections.synchronizedList(new ArrayList<>());

        // when: 동시 Enqueue (예약)
        for (int i = 0; i < threadCount; i++) {
            pool.execute(() -> {
                try {
                    start.await();
                    // SaveUserCouponCommand는 (userId, couponId, policyId, ...)
                    SaveUserCouponCommand cmd = new SaveUserCouponCommand(
                            /*userId*/ 1L,
                            /*couponId(=couponIssueId)*/ couponIssueId,
                            /*policyId*/ 1L,
                            CouponPolicyType.RATE, 10.0f, 30,
                            LocalDateTime.now().plusDays(30)
                    );
                    enqueueCouponIssueUseCase.enqueue(cmd);
                } catch (Throwable t) {
                    errors.add(t);
                } finally {
                    done.countDown();
                }
            });
        }
        start.countDown();
        done.await();
        pool.shutdown();

        // and: 테스트 스레드에서 직접 drain (worker 대행)
        int drained = 0;
        while (true) {
            ProcessResult r = processCouponIssueUseCase.process(couponIssueId);
            if (r.status() == ProcessResult.Status.NOT_FOUND) break; // 큐 비었음
            // SOLD_OUT/ALREADY_ISSUED도 1건 처리로 간주
            drained++;
        }

        // then
        assertThat(errors).isEmpty();
        assertThat(drained).isEqualTo(threadCount);

        CouponIssue updated = couponIssueRepository.findById(couponIssueId);
        assertThat(updated.getRemaining()).isEqualTo(initialRemaining - threadCount);
    }
} 