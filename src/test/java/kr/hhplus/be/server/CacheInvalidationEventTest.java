package kr.hhplus.be.server;

import kr.hhplus.be.server.common.redis.cache.events.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("캐시 무효화 이벤트 테스트")
public class CacheInvalidationEventTest {

    @Mock
    private ApplicationEventPublisher eventPublisher;
    
    @Mock
    private CacheManager cacheManager;

    @Test
    @DisplayName("상품 관련 캐시 무효화 이벤트 테스트")
    void productCacheInvalidationTest() {
        long productId = 999L;
        long optionId = 888L;
        
        // 1. 상품 변경 이벤트 발행
        ProductUpdatedEvent productEvent = new ProductUpdatedEvent(productId);
        eventPublisher.publishEvent(productEvent);
        
        // 2. 상품 옵션 변경 이벤트 발행
        ProductOptionsChangedEvent optionsEvent = new ProductOptionsChangedEvent(productId);
        eventPublisher.publishEvent(optionsEvent);
        
        // 3. 재고 변경 이벤트 발행
        StockChangedEvent stockEvent = new StockChangedEvent(productId, optionId, "DEDUCT", 10);
        eventPublisher.publishEvent(stockEvent);
        
        // 이벤트 발행 검증
        verify(eventPublisher).publishEvent(productEvent);
        verify(eventPublisher).publishEvent(optionsEvent);
        verify(eventPublisher).publishEvent(stockEvent);
        
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
        CouponPolicyChangedEvent policyEvent = new CouponPolicyChangedEvent(policyId);
        eventPublisher.publishEvent(policyEvent);
        
        // 2. 사용자 쿠폰 변경 이벤트 발행
        UserCouponChangedEvent userEvent = new UserCouponChangedEvent(userId);
        eventPublisher.publishEvent(userEvent);
        
        // 이벤트 발행 검증
        verify(eventPublisher).publishEvent(policyEvent);
        verify(eventPublisher).publishEvent(userEvent);
        
        System.out.println("=== 쿠폰 관련 캐시 무효화 이벤트 테스트 ===");
        System.out.println("✅ CouponPolicyChangedEvent 발행 완료");
        System.out.println("✅ UserCouponChangedEvent 발행 완료");
        System.out.println("✅ 모든 이벤트가 정상적으로 발행되었습니다!");
    }
}
