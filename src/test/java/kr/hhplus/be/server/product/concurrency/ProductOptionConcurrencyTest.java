package kr.hhplus.be.server.product.concurrency;

import kr.hhplus.be.server.product.application.service.DeductStockService;
import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.domain.model.ProductOption;
import kr.hhplus.be.server.product.domain.repository.ProductOptionRepository;
import kr.hhplus.be.server.product.domain.repository.ProductRepository;
import kr.hhplus.be.server.product.domain.type.ProductOptionStatus;
import kr.hhplus.be.server.product.domain.type.ProductStatus;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("통합 테스트 - 상품 옵션 동시성 제어 테스트")
public class ProductOptionConcurrencyTest {

    @Autowired
    private DeductStockService deductStockService;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("50개의 동시 요청에도 상품 옵션 재고가 정확히 차감된다")
    void deductStock_concurrency_success() throws InterruptedException {
        // given
        int initialStock = 100;
        int threadCount = 50;
        int deductQuantity = 1;

        // 상품 생성
        Product product = productRepository.insertOrUpdate(new Product(null, "테스트 상품", ProductStatus.ON_SALE, java.time.LocalDateTime.now(), null));
        
        // 상품 옵션 생성
        ProductOption option = new ProductOption(null, product.getProductId(), "테스트 옵션", ProductOptionStatus.ON_SALE, 10000L, initialStock, java.time.LocalDateTime.now(), null);
        ProductOption savedOption = productOptionRepository.insertOrUpdate(option);
        long optionId = savedOption.getOptionId();

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when - 동시에 같은 상품 옵션의 재고 차감
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            executorService.execute(() -> {
                try {
                    System.out.println("Thread " + threadIndex + " 시작");
                    deductStockService.deductStock(optionId, deductQuantity);
                    System.out.println("Thread " + threadIndex + " 성공");
                } catch (Exception e) {
                    System.err.println("Thread " + threadIndex + " 실패: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then - 재고 검증
        ProductOption updatedOption = productOptionRepository.findOptionByOptionId(optionId);
        assertThat(updatedOption.getStock()).isEqualTo(initialStock - deductQuantity * threadCount);

        executorService.shutdown();
    }
} 