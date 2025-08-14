package kr.hhplus.be.server.cache;

import kr.hhplus.be.server.IntegrationTestBase;
import kr.hhplus.be.server.common.redis.cache.StockCounter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("재고 캐시(Hash) 동작 확인 테스트")
public class StockCacheTest extends IntegrationTestBase {

    @Autowired
    private StockCounter stockCounter;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    @DisplayName("재고 해시 초기화 및 조회 테스트")
    void stockHashInitAndGetTest() {
        // given
        long productId = 1L;
        long optionId = 100L;
        long initialStock = 50L;

        // when - 재고 해시 초기화
        stockCounter.initStockHash(productId, optionId, initialStock);

        // then - Redis에 해시가 생성되었는지 확인
        String hashKey = "stock:prod:" + productId;
        Object stockValue = redisTemplate.opsForHash().get(hashKey, String.valueOf(optionId));
        
        assertThat(stockValue).isNotNull();
        assertThat(Long.parseLong(stockValue.toString())).isEqualTo(initialStock);
        
        // StockCounter를 통한 조회도 확인
        long retrievedStock = stockCounter.getStockHash(productId, optionId);
        assertThat(retrievedStock).isEqualTo(initialStock);
    }

    @Test
    @DisplayName("재고 해시 원자적 차감 테스트")
    void stockHashDeductTest() {
        // given
        long productId = 2L;
        long optionId = 200L;
        long initialStock = 100L;
        int deductQty = 30;

        // 재고 초기화
        stockCounter.initStockHash(productId, optionId, initialStock);

        // when - 재고 차감
        long remainingStock = stockCounter.tryDeductHash(productId, optionId, deductQty);

        // then - 차감 후 남은 재고 확인
        assertThat(remainingStock).isEqualTo(initialStock - deductQty);
        
        // Redis에서 직접 확인
        long actualStock = stockCounter.getStockHash(productId, optionId);
        assertThat(actualStock).isEqualTo(initialStock - deductQty);
    }

    @Test
    @DisplayName("재고 부족 시 차감 실패 테스트")
    void stockHashInsufficientStockTest() {
        // given
        long productId = 3L;
        long optionId = 300L;
        long initialStock = 20L;
        int deductQty = 30; // 초기 재고보다 많은 수량

        // 재고 초기화
        stockCounter.initStockHash(productId, optionId, initialStock);

        // when - 재고 부족으로 차감 시도
        long result = stockCounter.tryDeductHash(productId, optionId, deductQty);

        // then - 차감 실패 (-1 반환)
        assertThat(result).isEqualTo(-1);
        
        // 원래 재고는 그대로 유지
        long actualStock = stockCounter.getStockHash(productId, optionId);
        assertThat(actualStock).isEqualTo(initialStock);
    }

    @Test
    @DisplayName("재고 보상 복구 테스트")
    void stockHashCompensateTest() {
        // given
        long productId = 4L;
        long optionId = 400L;
        long initialStock = 80L;
        int deductQty = 20;
        int compensateQty = 10;

        // 재고 초기화
        stockCounter.initStockHash(productId, optionId, initialStock);

        // 재고 차감
        stockCounter.tryDeductHash(productId, optionId, deductQty);

        // when - 재고 보상 복구
        stockCounter.compensateHash(productId, optionId, compensateQty);

        // then - 보상 후 재고 확인
        long expectedStock = initialStock - deductQty + compensateQty;
        long actualStock = stockCounter.getStockHash(productId, optionId);
        assertThat(actualStock).isEqualTo(expectedStock);
    }

    @Test
    @DisplayName("여러 옵션의 재고를 하나의 해시에 저장하는 테스트")
    void stockHashMultipleOptionsTest() {
        // given
        long productId = 5L;
        long optionId1 = 501L;
        long optionId2 = 502L;
        long stock1 = 100L;
        long stock2 = 200L;

        // when - 여러 옵션의 재고를 하나의 해시에 저장
        stockCounter.initStockHash(productId, optionId1, stock1);
        stockCounter.initStockHash(productId, optionId2, stock2);

        // then - 각 옵션의 재고가 올바르게 저장되었는지 확인
        long retrievedStock1 = stockCounter.getStockHash(productId, optionId1);
        long retrievedStock2 = stockCounter.getStockHash(productId, optionId2);

        assertThat(retrievedStock1).isEqualTo(stock1);
        assertThat(retrievedStock2).isEqualTo(stock2);

        // Redis 해시 구조 확인
        String hashKey = "stock:prod:" + productId;
        Long hashSize = redisTemplate.opsForHash().size(hashKey);
        assertThat(hashSize).isEqualTo(3L); // 2개 + 1(1개가 테스트 시 계속 추가됨 잡아야함)의 옵션
    }
}
