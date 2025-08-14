package kr.hhplus.be.server;

import kr.hhplus.be.server.common.redis.cache.events.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("캐시 무효화 이벤트 테스트")
public class CacheInvalidationEventTest {

    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    @Autowired
    private CacheManager cacheManager;

    @Test
    @DisplayName("상품 관련 캐시 무효화 이벤트 테스트")
    void productCacheInvalidationTest() {
        long productId = 999L;
        long optionId = 888L;
        
        // 1. 상품 변경 이벤트 발행
        eventPublisher.publishEvent(new ProductUpdatedEvent(productId));
        
        // 2. 상품 옵션 변경 이벤트 발행
        eventPublisher.publishEvent(new ProductOptionsChangedEvent(productId));
        
        // 3. 재고 변경 이벤트 발행
        eventPublisher.publishEvent(new StockChangedEvent(productId, optionId, "DEDUCT", 10));
        
        System.out.println("=== 상품 관련 캐시 무효화 이벤트 테스트 ===");
        System.out.println("✅ ProductUpdatedEvent 발행 완료");
        System.out.println("✅ ProductOptionsChangedEvent 발행 완료");
        System.out.println("✅ StockChangedEvent 발행 완료");
        System.out.println("✅ 모든 이벤트가 정상적으로 발행되었습니다!");
    }
    
    @Test
    @DisplayName("쿠폰 관련 캐시 무효화 이벤트 테스트")
    void couponCacheInvalidationTest() {
        long policyId = 777L;
        long userId = 666L;
        
        // 1. 쿠폰 정책 변경 이벤트 발행
        eventPublisher.publishEvent(new CouponPolicyChangedEvent(policyId));
        
        // 2. 사용자 쿠폰 변경 이벤트 발행 (기존 이벤트)
        eventPublisher.publishEvent(new UserCouponChangedEvent(userId));
        
        System.out.println("=== 쿠폰 관련 캐시 무효화 이벤트 테스트 ===");
        System.out.println("✅ CouponPolicyChangedEvent 발행 완료");
        System.out.println("✅ UserCouponChangedEvent 발행 완료");
        System.out.println("✅ 모든 이벤트가 정상적으로 발행되었습니다!");
    }
}
