package kr.hhplus.be.server.product.integration;

import kr.hhplus.be.server.IntegrationTestBase;
import kr.hhplus.be.server.common.redis.cache.StockCounter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("StockCounter 간단 테스트")
public class StockCounterSimpleTest extends IntegrationTestBase {

    @Autowired
    private StockCounter stockCounter;

    @Test
    @DisplayName("Redis Hash 기반 재고 관리 테스트")
    void stockHashTest() {
        long productId = 999L;
        long optionId = 888L;
        int initialStock = 100;
        
        // 1. 재고 초기화
        stockCounter.initStockHash(productId, optionId, initialStock);
        
        // 2. 재고 조회 확인
        long retrievedStock = stockCounter.getStockHash(productId, optionId);
        assertThat(retrievedStock).isEqualTo(initialStock);
        
        // 3. 재고 차감 테스트
        int deductQty = 30;
        long remainingStock = stockCounter.tryDeductHash(productId, optionId, deductQty);
        assertThat(remainingStock).isEqualTo(initialStock - deductQty);
        
        // 4. 차감 후 재고 확인
        long actualStock = stockCounter.getStockHash(productId, optionId);
        assertThat(actualStock).isEqualTo(initialStock - deductQty);
        
        // 5. 재고 복구 테스트
        int compensateQty = 10;
        stockCounter.compensateHash(productId, optionId, compensateQty);
        
        // 6. 복구 후 재고 확인
        long finalStock = stockCounter.getStockHash(productId, optionId);
        assertThat(finalStock).isEqualTo(initialStock - deductQty + compensateQty);
        
        System.out.println("=== Redis Hash 기반 재고 관리 테스트 결과 ===");
        System.out.printf("초기 재고: %d%n", initialStock);
        System.out.printf("차감 후 재고: %d%n", actualStock);
        System.out.printf("복구 후 재고: %d%n", finalStock);
        System.out.println("모든 테스트 통과!");
    }
}
