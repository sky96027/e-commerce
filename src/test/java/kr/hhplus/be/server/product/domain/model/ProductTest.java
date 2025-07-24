package kr.hhplus.be.server.product.domain.model;

import kr.hhplus.be.server.product.domain.type.ProductStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ProductTest {

    @Test
    @DisplayName("Product 생성자 정상 작동")
    void constructor_createsProductCorrectly() {
        // given
        long productId = 1L;
        String name = "노트북";
        ProductStatus status = ProductStatus.ON_SALE;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiredAt = createdAt.plusDays(30);

        // when
        Product product = new Product(productId, name, status, createdAt, expiredAt);

        // then
        assertThat(product.getProductId()).isEqualTo(productId);
        assertThat(product.getProductName()).isEqualTo(name);
        assertThat(product.getStatus()).isEqualTo(status);
        assertThat(product.getCreatedAt()).isEqualTo(createdAt);
        assertThat(product.getExpiredAt()).isEqualTo(expiredAt);
    }
}