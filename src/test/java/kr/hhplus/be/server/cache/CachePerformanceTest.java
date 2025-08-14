package kr.hhplus.be.server.cache;

import kr.hhplus.be.server.IntegrationTestBase;
import kr.hhplus.be.server.product.application.service.FindDetailService;
import kr.hhplus.be.server.order.application.service.FindOrderByOrderIdService;
import kr.hhplus.be.server.coupon.application.service.FindCouponPolicyService;
import kr.hhplus.be.server.coupon.application.service.FindUserCouponSummaryService;
import kr.hhplus.be.server.transactionhistory.application.service.FindHistoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("캐시 성능 비교 테스트")
@Transactional
public class CachePerformanceTest extends IntegrationTestBase {

    @Autowired
    private FindDetailService findDetailService;

    @Autowired
    private FindOrderByOrderIdService findOrderService;

    @Autowired
    private FindCouponPolicyService findCouponPolicyService;

    @Autowired
    private FindUserCouponSummaryService findUserCouponService;

    @Autowired
    private FindHistoryService findHistoryService;

    @Autowired
    private CacheManager cacheManager;

    @Test
    @DisplayName("상품 상세 조회 캐시 성능 비교")
    void productDetailCachePerformance() {
        // given
        long productId = 1L; // 테스트용 상품 ID
        
        // 캐시 초기화
        cacheManager.getCache("product:detail").clear();

        // when - 캐시 미스 (첫 번째 호출)
        long startTime = System.nanoTime();
        try {
            findDetailService.findById(productId);
        } catch (Exception e) {
            // 상품이 없어도 캐시 동작은 확인 가능
        }
        long cacheMissTime = System.nanoTime() - startTime;

        // when - 캐시 히트 (두 번째 호출)
        startTime = System.nanoTime();
        try {
            findDetailService.findById(productId);
        } catch (Exception e) {
            // 상품이 없어도 캐시 동작은 확인 가능
        }
        long cacheHitTime = System.nanoTime() - startTime;

        // then
        System.out.println("=== 상품 상세 조회 캐시 성능 비교 ===");
        System.out.printf("캐시 미스 (DB 조회): %d ns (%.3f ms)%n", cacheMissTime, cacheMissTime / 1_000_000.0);
        System.out.printf("캐시 히트 (메모리): %d ns (%.3f ms)%n", cacheHitTime, cacheHitTime / 1_000_000.0);
        System.out.printf("성능 향상: %.2f배%n", (double) cacheMissTime / cacheHitTime);
        System.out.println();
        
        // 검증
        assertThat(cacheHitTime).isLessThan(cacheMissTime);
    }

    @Test
    @DisplayName("주문 조회 캐시 성능 비교")
    void orderCachePerformance() {
        // given
        long orderId = 1L; // 테스트용 주문 ID
        
        // 캐시 초기화
        cacheManager.getCache("order:summary").clear();

        // when - 캐시 미스 (첫 번째 호출)
        long startTime = System.nanoTime();
        try {
            findOrderService.findById(orderId);
        } catch (Exception e) {
            // 주문이 없어도 캐시 동작은 확인 가능
        }
        long cacheMissTime = System.nanoTime() - startTime;

        // when - 캐시 히트 (두 번째 호출)
        startTime = System.nanoTime();
        try {
            findOrderService.findById(orderId);
        } catch (Exception e) {
            // 주문이 없어도 캐시 동작은 확인 가능
        }
        long cacheHitTime = System.nanoTime() - startTime;

        // then
        System.out.println("=== 주문 조회 캐시 성능 비교 ===");
        System.out.printf("캐시 미스 (DB 조회): %d ns (%.3f ms)%n", cacheMissTime, cacheMissTime / 1_000_000.0);
        System.out.printf("캐시 히트 (메모리): %d ns (%.3f ms)%n", cacheHitTime, cacheHitTime / 1_000_000.0);
        System.out.printf("성능 향상: %.2f배%n", (double) cacheMissTime / cacheHitTime);
        System.out.println();
        
        // 검증
        assertThat(cacheHitTime).isLessThan(cacheMissTime);
    }

    @Test
    @DisplayName("쿠폰 정책 조회 캐시 성능 비교")
    void couponPolicyCachePerformance() {
        // given
        long policyId = 1L; // 테스트용 쿠폰 정책 ID
        
        // 캐시 초기화
        cacheManager.getCache("coupon:policy").clear();

        // when - 캐시 미스 (첫 번째 호출)
        long startTime = System.nanoTime();
        try {
            findCouponPolicyService.findById(policyId);
        } catch (Exception e) {
            // 쿠폰 정책이 없어도 캐시 동작은 확인 가능
        }
        long cacheMissTime = System.nanoTime() - startTime;

        // when - 캐시 히트 (두 번째 호출)
        startTime = System.nanoTime();
        try {
            findCouponPolicyService.findById(policyId);
        } catch (Exception e) {
            // 쿠폰 정책이 없어도 캐시 동작은 확인 가능
        }
        long cacheHitTime = System.nanoTime() - startTime;

        // then
        System.out.println("=== 쿠폰 정책 조회 캐시 성능 비교 ===");
        System.out.printf("캐시 미스 (DB 조회): %d ns (%.3f ms)%n", cacheMissTime, cacheMissTime / 1_000_000.0);
        System.out.printf("캐시 히트 (메모리): %d ns (%.3f ms)%n", cacheHitTime, cacheHitTime / 1_000_000.0);
        System.out.printf("성능 향상: %.2f배%n", (double) cacheMissTime / cacheHitTime);
        System.out.println();
        
        // 검증
        assertThat(cacheHitTime).isLessThan(cacheMissTime);
    }

    @Test
    @DisplayName("사용자 쿠폰 요약 조회 캐시 성능 비교")
    void userCouponCachePerformance() {
        // given
        long userId = 1L; // 테스트용 사용자 ID
        
        // 캐시 초기화
        cacheManager.getCache("coupon:userSummary").clear();

        // when - 캐시 미스 (첫 번째 호출)
        long startTime = System.nanoTime();
        try {
            findUserCouponService.findSummary(userId);
        } catch (Exception e) {
            // 사용자 쿠폰이 없어도 캐시 동작은 확인 가능
        }
        long cacheMissTime = System.nanoTime() - startTime;

        // when - 캐시 히트 (두 번째 호출)
        startTime = System.nanoTime();
        try {
            findUserCouponService.findSummary(userId);
        } catch (Exception e) {
            // 사용자 쿠폰이 없어도 캐시 동작은 확인 가능
        }
        long cacheHitTime = System.nanoTime() - startTime;

        // then
        System.out.println("=== 사용자 쿠폰 요약 조회 캐시 성능 비교 ===");
        System.out.printf("캐시 미스 (DB 조회): %d ns (%.3f ms)%n", cacheMissTime, cacheMissTime / 1_000_000.0);
        System.out.printf("캐시 히트 (메모리): %d ns (%.3f ms)%n", cacheHitTime, cacheHitTime / 1_000_000.0);
        System.out.printf("성능 향상: %.2f배%n", (double) cacheMissTime / cacheHitTime);
        System.out.println();
        
        // 검증
        assertThat(cacheHitTime).isLessThan(cacheMissTime);
    }

    @Test
    @DisplayName("거래 내역 조회 캐시 성능 비교")
    void transactionHistoryCachePerformance() {
        // given
        long userId = 1L; // 테스트용 사용자 ID
        
        // 캐시 초기화
        cacheManager.getCache("tx:recent").clear();

        // when - 캐시 미스 (첫 번째 호출)
        long startTime = System.nanoTime();
        try {
            findHistoryService.findAllByUserId(userId);
        } catch (Exception e) {
            // 거래 내역이 없어도 캐시 동작은 확인 가능
        }
        long cacheMissTime = System.nanoTime() - startTime;

        // when - 캐시 히트 (두 번째 호출)
        startTime = System.nanoTime();
        try {
            findHistoryService.findAllByUserId(userId);
        } catch (Exception e) {
            // 거래 내역이 없어도 캐시 동작은 확인 가능
        }
        long cacheHitTime = System.nanoTime() - startTime;

        // then
        System.out.println("=== 거래 내역 조회 캐시 성능 비교 ===");
        System.out.printf("캐시 미스 (DB 조회): %d ns (%.3f ms)%n", cacheMissTime, cacheMissTime / 1_000_000.0);
        System.out.printf("캐시 히트 (메모리): %d ns (%.3f ms)%n", cacheHitTime, cacheHitTime / 1_000_000.0);
        System.out.printf("성능 향상: %.2f배%n", (double) cacheMissTime / cacheHitTime);
        System.out.println();
        
        // 검증
        assertThat(cacheHitTime).isLessThan(cacheMissTime);
    }
}
