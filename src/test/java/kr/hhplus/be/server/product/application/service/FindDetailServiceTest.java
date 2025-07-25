package kr.hhplus.be.server.product.application.service;

import kr.hhplus.be.server.product.application.dto.ProductDto;
import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.domain.repository.ProductRepository;
import kr.hhplus.be.server.product.domain.type.ProductStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindDetailServiceTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    FindDetailService findDetailService;

    @Test
    @DisplayName("상품 ID로 상세 조회 성공")
    void findById_success() {
        // given
        long productId = 1L;
        String name = "맥북";
        ProductStatus status = ProductStatus.ON_SALE;
        LocalDateTime now = LocalDateTime.now();

        Product product = new Product(productId, name, status, now, null);
        when(productRepository.selectById(productId)).thenReturn(product);

        // when
        ProductDto result = findDetailService.findById(productId);

        // then
        assertThat(result.productId()).isEqualTo(productId);
        assertThat(result.productName()).isEqualTo(name);
        assertThat(result.status()).isEqualTo(status);
        assertThat(result.createdAt()).isEqualTo(now);
        assertThat(result.expiredAt()).isNull();
        verify(productRepository, times(1)).selectById(productId);
    }

    @Test
    @DisplayName("상품이 존재하지 않으면 예외 발생")
    void findById_notFound_throwsException() {
        // given
        long productId = 999L;
        when(productRepository.selectById(productId)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> findDetailService.findById(productId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상품을 찾을 수 없습니다");
        verify(productRepository, times(1)).selectById(productId);
    }
}