package kr.hhplus.be.server.product.integration;

import kr.hhplus.be.server.product.application.service.DeductStockService;
import kr.hhplus.be.server.product.domain.model.ProductOption;
import kr.hhplus.be.server.product.domain.repository.ProductOptionRepository;
import kr.hhplus.be.server.product.domain.type.ProductOptionStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@DisplayName("통합 테스트 - 상품 재고 차감")
public class ProductOptionIntegrationTest {

    @Autowired
    private DeductStockService deductStockService;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Test
    @DisplayName("재고를 정상적으로 차감한다")
    void deductStock_success() {
        // given
        ProductOption option = new ProductOption(null, 1L, "옵션1", ProductOptionStatus.ON_SALE, 10000L, 10, LocalDateTime.now(), null);
        productOptionRepository.insertOrUpdate(option);

        List<ProductOption> options = productOptionRepository.findOptionsByProductId(1L);
        assertThat(options).isNotEmpty();
        long actualOptionId = options.get(0).getOptionId();

        // 저장 확인
        ProductOption savedOption = productOptionRepository.findOptionByOptionId(actualOptionId);
        assertThat(savedOption).isNotNull();
        assertThat(savedOption.getStock()).isEqualTo(10);

        // when
        deductStockService.deductStock(actualOptionId, 2);

        // then
        ProductOption updatedOption = productOptionRepository.findOptionByOptionId(actualOptionId);
        assertThat(updatedOption).isNotNull();
        assertThat(updatedOption.getStock()).isEqualTo(8);
    }

    @Test
    @DisplayName("재고 부족 시 예외 발생")
    void deductStock_insufficientStock_throwsException() {
        // given
        ProductOption option = new ProductOption(null, 1L, "옵션2", ProductOptionStatus.ON_SALE, 10000L, 1, LocalDateTime.now(), null);
        ProductOption savedOption = productOptionRepository.insertOrUpdate(option); // ← 저장된 객체 받기

        // when & then
        assertThatThrownBy(() -> deductStockService.deductStock(savedOption.getOptionId(), 2)) // ← 저장된 ID 사용
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("재고가 부족합니다");
    }

    @Test
    @DisplayName("음수 차감 시 예외 발생")
    void deductStock_negativeAmount_throwsException() {
        // given
        ProductOption option = new ProductOption(null, 1L, "옵션3", ProductOptionStatus.ON_SALE, 10000L, 10, LocalDateTime.now(), null);
        ProductOption savedOption = productOptionRepository.insertOrUpdate(option); // ← 저장된 객체 받기

        // when & then
        assertThatThrownBy(() -> deductStockService.deductStock(savedOption.getOptionId(), -1)) // ← 저장된 ID 사용
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("차감량은 음수일 수 없습니다");
    }
} 