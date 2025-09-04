package kr.hhplus.be.server.coupon.concurrency;

import kr.hhplus.be.server.IntegrationTestBase;
import kr.hhplus.be.server.coupon.application.dto.SaveUserCouponCommand;
import kr.hhplus.be.server.coupon.application.usecase.EnqueueCouponIssueUseCase;
import kr.hhplus.be.server.coupon.application.usecase.SaveUserCouponUseCase;
import kr.hhplus.be.server.coupon.domain.model.CouponIssue;
import kr.hhplus.be.server.coupon.domain.repository.CouponIssueRepository;
import kr.hhplus.be.server.coupon.domain.type.CouponIssueStatus;
import kr.hhplus.be.server.coupon.domain.type.CouponPolicyType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.time.Duration;
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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("통합 테스트 - 쿠폰 발급 동시성 제어 테스트")
public class CouponConcurrencyTest extends IntegrationTestBase {

    @Autowired
    private EnqueueCouponIssueUseCase enqueueCouponIssueUseCase;

    @Autowired
    private CouponIssueRepository couponIssueRepository;

    @Autowired
    Environment env;

    @Test
    @Order(1)
    void sanity() {
        System.out.println("bootstrap = " + env.getProperty("spring.kafka.bootstrap-servers"));
    }

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
    @DisplayName("50개의 동시 예약 후 Kafka Consumer가 정확히 발급 처리한다")
    void enqueue_then_consume_success() throws Exception {
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

        // when: 동시 Enqueue (Kafka 이벤트 발행)

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
                    enqueueCouponIssueUseCase.enqueue(cmd); // Kafka 이벤트 발행
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
        // then: Kafka Consumer가 처리 끝날 때까지 대기
        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> {
                    CouponIssue updated = couponIssueRepository.findById(couponIssueId);
                    assertThat(updated.getRemaining()).isEqualTo(initialRemaining - threadCount);
                });

        assertThat(errors).isEmpty();
    }
} 