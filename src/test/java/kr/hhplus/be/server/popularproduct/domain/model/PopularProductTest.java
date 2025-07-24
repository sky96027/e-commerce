package kr.hhplus.be.server.popularproduct.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PopularProductTest {
    @Test
    @DisplayName("PopularProduct 생성자 및 필드 값 정상 할당")
    void constructor_success() {
        // given
        long id = 1L;
        long productId = 2L;
        int totalSoldQuantity = 100;
        int rank = 1;
        LocalDate referenceDate = LocalDate.now();
        LocalDateTime createdAt = LocalDateTime.now();

        // when
        PopularProduct product = new PopularProduct(id, productId, totalSoldQuantity, rank, referenceDate, createdAt);

        // then
        assertThat(product.getId()).isEqualTo(id);
        assertThat(product.getProductId()).isEqualTo(productId);
        assertThat(product.getTotalSoldQuantity()).isEqualTo(totalSoldQuantity);
        assertThat(product.getRank()).isEqualTo(rank);
        assertThat(product.getReferenceDate()).isEqualTo(referenceDate);
        assertThat(product.getCreatedAt()).isEqualTo(createdAt);
    }
} 