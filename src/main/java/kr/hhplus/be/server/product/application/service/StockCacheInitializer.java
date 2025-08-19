package kr.hhplus.be.server.product.application.service;

import kr.hhplus.be.server.common.redis.cache.StockCounter;
import kr.hhplus.be.server.product.domain.repository.ProductOptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * 애플리케이션 시작 시 Redis 재고 캐시를 초기화하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockCacheInitializer {
    
    private final StockCounter stockCounter;
    private final ProductOptionRepository productOptionRepository;
    
    @EventListener(ApplicationReadyEvent.class)
    public void initializeStockCache() {
        log.info("Redis 재고 캐시 초기화 시작...");
        
        try {
            // 간단하게 테스트용 상품 ID 1번만 초기화
            // 실제로는 상품 목록을 조회해서 모든 상품을 초기화해야 함
            long testProductId = 1L;
            var options = productOptionRepository.findOptionsByProductId(testProductId);
            
            options.forEach(option -> {
                stockCounter.initStockHash(
                    option.getProductId(), 
                    option.getOptionId(), 
                    option.getStock()
                );
                log.debug("재고 캐시 초기화: 상품ID={}, 옵션ID={}, 재고={}", 
                    option.getProductId(), option.getOptionId(), option.getStock());
            });
            
            log.info("Redis 재고 캐시 초기화 완료 (상품ID: {})", testProductId);
        } catch (Exception e) {
            log.warn("Redis 재고 캐시 초기화 실패: {}", e.getMessage());
        }
    }
}
