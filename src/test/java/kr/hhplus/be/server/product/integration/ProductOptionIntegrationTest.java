package kr.hhplus.be.server.product.integration;

import kr.hhplus.be.server.product.application.service.DeductStockService; // 서비스명 유지 시 그대로 사용
import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.domain.model.ProductOption;
import kr.hhplus.be.server.product.domain.repository.ProductOptionRepository;
import kr.hhplus.be.server.product.domain.repository.ProductRepository;
import kr.hhplus.be.server.product.domain.type.ProductOptionStatus;
import kr.hhplus.be.server.product.domain.type.ProductStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("통합 테스트 - 상품 재고 차감 (원자적 UPDATE 기반)")
public class ProductOptionIntegrationTest {

    @Autowired
    private DeductStockService deductStockService;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("재고를 정상적으로 차감한다")
    void deductStock_success() {
        // given
        Product product = new Product(null, "테스트 상품", ProductStatus.ON_SALE, LocalDateTime.now(), null);
        Product savedProduct = productRepository.insertOrUpdate(product);

        ProductOption option = new ProductOption(
                null, savedProduct.getProductId(), "옵션1",
                ProductOptionStatus.ON_SALE, 10000L, 10,
                LocalDateTime.now(), null
        );
        productOptionRepository.insertOrUpdate(option);

        List<ProductOption> options = productOptionRepository.findOptionsByProductId(savedProduct.getProductId());
        assertThat(options).isNotEmpty();
        long optionId = options.get(0).getOptionId();

        // 저장 확인
        ProductOption before = productOptionRepository.findOptionByOptionId(optionId);
        assertThat(before).isNotNull();
        assertThat(before.getStock()).isEqualTo(10);

        // when
        deductStockService.deductStock(optionId, 2);

        // then
        ProductOption after = productOptionRepository.findOptionByOptionId(optionId);
        assertThat(after).isNotNull();
        assertThat(after.getStock()).isEqualTo(8);
    }

    @Test
    @DisplayName("재고 부족 시 예외 발생")
    void deductStock_insufficientStock_throwsException() {
        // given
        Product product = new Product(null, "테스트 상품2", ProductStatus.ON_SALE, LocalDateTime.now(), null);
        Product savedProduct = productRepository.insertOrUpdate(product);

        ProductOption option = new ProductOption(
                null, savedProduct.getProductId(), "옵션2",
                ProductOptionStatus.ON_SALE, 10000L, 1,
                LocalDateTime.now(), null
        );
        ProductOption savedOption = productOptionRepository.insertOrUpdate(option);

        // when & then
        assertThatThrownBy(() -> deductStockService.deductStock(savedOption.getOptionId(), 2))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("음수 차감 시 예외 발생")
    void deductStock_negativeAmount_throwsException() {
        // given
        Product product = new Product(null, "테스트 상품3", ProductStatus.ON_SALE, LocalDateTime.now(), null);
        Product savedProduct = productRepository.insertOrUpdate(product);

        ProductOption option = new ProductOption(
                null, savedProduct.getProductId(), "옵션3",
                ProductOptionStatus.ON_SALE, 10000L, 10,
                LocalDateTime.now(), null
        );
        ProductOption savedOption = productOptionRepository.insertOrUpdate(option);

        // when & then
        assertThatThrownBy(() -> deductStockService.deductStock(savedOption.getOptionId(), -1))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
