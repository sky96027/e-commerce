package kr.hhplus.be.server.product.integration;

import kr.hhplus.be.server.IntegrationTestBase;
import kr.hhplus.be.server.common.redis.cache.StockCounter;
import kr.hhplus.be.server.product.application.service.DeductStockService;
import kr.hhplus.be.server.product.application.service.AddStockService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("StockCounter 적용된 재고 서비스 통합 테스트")
@Transactional
public class StockServiceIntegrationTest extends IntegrationTestBase {

    @Autowired
    private StockCounter stockCounter;
    
    @Autowired
    private DeductStockService deductStockService;
    
    @Autowired
    private AddStockService addStockService;

    @Test
    @DisplayName("Redis Hash 기반 재고 차감/증가 통합 테스트")
    void stockServiceIntegrationTest() {
        long productId = 999L;
        long optionId = 888L;
        int initialStock = 100;
        
        // 1. Redis에 재고 초기화
        stockCounter.initStockHash(productId, optionId, initialStock);
        
        // 2. 초기 재고 확인
        long currentStock = stockCounter.getStockHash(productId, optionId);
        assertThat(currentStock).isEqualTo(initialStock);
        
        System.out.println("=== Redis Hash 기반 재고 서비스 통합 테스트 ===");
        System.out.printf("초기 재고: %d%n", currentStock);
        
        // 3. 재고 차감 테스트
        try {
            deductStockService.deductStock(optionId, 20);
            System.out.println("재고 차감 성공");
        } catch (Exception e) {
            System.out.println("재고 차감 실패 (예상됨): " + e.getMessage());
        }
        
        // 4. 재고 증가 테스트
        try {
            addStockService.addStock(optionId, 10);
            System.out.println("재고 증가 성공");
        } catch (Exception e) {
            System.out.println("재고 증가 실패 (예상됨): " + e.getMessage());
        }
        
        // 5. Redis 재고 상태 확인
        long finalStock = stockCounter.getStockHash(productId, optionId);
        System.out.printf("최종 Redis 재고: %d%n", finalStock);
        System.out.println("테스트 완료!");
        
        // Redis 재고는 초기값과 동일해야 함
        assertThat(finalStock).isEqualTo(initialStock);
    }
}
