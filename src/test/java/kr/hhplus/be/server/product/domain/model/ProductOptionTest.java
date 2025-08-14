package kr.hhplus.be.server.product.domain.model;

import kr.hhplus.be.server.product.domain.type.ProductOptionStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class ProductOptionTest {

    @Test
    @DisplayName("재고 차감 성공")
    void deduct_success() {
        // given
        ProductOption option = new ProductOption(1L, 2L, "옵션1", ProductOptionStatus.ON_SALE, 10000L, 10, LocalDateTime.now(), null);

        // when
        ProductOption updated = option.deduct(3);

        // then
        assertThat(updated.getStock()).isEqualTo(7);
        assertThat(updated.getOptionId()).isEqualTo(option.getOptionId());
        assertThat(updated.getProductId()).isEqualTo(option.getProductId());
    }

    // db update 로 책임 이동
    /*@Test
    @DisplayName("차감량이 음수일 경우 예외 발생")
    void deduct_negativeAmount_throwsException() {
        // given
        ProductOption option = new ProductOption(1L, 2L, "옵션1", ProductOptionStatus.ON_SALE, 10000L, 10, LocalDateTime.now(), null);

        // expect
        assertThatThrownBy(() -> option.deduct(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("차감량은 음수일 수 없습니다.");
    }*/

    @Test
    @DisplayName("재고 부족 시 예외 발생")
    void deduct_insufficientStock_throwsException() {
        // given
        ProductOption option = new ProductOption(1L, 2L, "옵션1", ProductOptionStatus.ON_SALE, 10000L, 2, LocalDateTime.now(), null);

        // expect
        assertThatThrownBy(() -> option.deduct(3))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("재고가 부족합니다.");
    }
}