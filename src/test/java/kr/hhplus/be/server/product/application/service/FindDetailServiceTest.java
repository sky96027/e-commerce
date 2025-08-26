package kr.hhplus.be.server.product.application.service;

import kr.hhplus.be.server.common.exception.RestApiException;
import kr.hhplus.be.server.product.application.dto.ProductDto;
import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.domain.repository.ProductRepository;
import kr.hhplus.be.server.product.domain.type.ProductStatus;
import kr.hhplus.be.server.product.exception.ProductErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class FindDetailServiceTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ValueOperations<String, Object> valueOps;

    @InjectMocks
    FindDetailService findDetailService;

    @BeforeEach
    void setup() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
    }

    @Test
    @DisplayName("상품 ID로 상세 조회 성공")
    void findById_success() {
        // given
        long productId = 1L;
        String name = "맥북";
        ProductStatus status = ProductStatus.ON_SALE;
        LocalDateTime now = LocalDateTime.now();

        Product product = new Product(productId, name, status, now, null);
        when(productRepository.findById(productId)).thenReturn(product);

        // when
        ProductDto result = findDetailService.findById(productId);

        // then
        assertThat(result.productId()).isEqualTo(productId);
        assertThat(result.productName()).isEqualTo(name);
        assertThat(result.status()).isEqualTo(status);
        assertThat(result.createdAt()).isEqualTo(now);
        assertThat(result.expiredAt()).isNull();
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("상품이 존재하지 않으면 예외 발생")
    void findById_notFound_throwsException() {
        // given
        long productId = 999L;
        when(productRepository.findById(productId))
                .thenThrow(new RestApiException(ProductErrorCode.PRODUCT_NOT_FOUND_ERROR));

        // when & then
        assertThatThrownBy(() -> findDetailService.findById(productId))
                .isInstanceOf(RestApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", ProductErrorCode.PRODUCT_NOT_FOUND_ERROR);
        verify(productRepository, times(1)).findById(productId);
    }
}