package kr.hhplus.be.server.product.application.service;

import kr.hhplus.be.server.product.application.dto.ProductOptionDto;
import kr.hhplus.be.server.product.domain.model.ProductOption;
import kr.hhplus.be.server.product.domain.repository.ProductOptionRepository;
import kr.hhplus.be.server.product.domain.type.ProductOptionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class FindProductOptionsServiceTest {

    @Mock
    ProductOptionRepository productOptionRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ValueOperations<String, Object> valueOps;

    @InjectMocks
    FindProductOptionsService findProductOptionsService;

    @BeforeEach
    void setup() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
    }

    @Test
    @DisplayName("상품 ID로 옵션 목록 조회 성공")
    void findByProductId_success() {
        // given
        long productId = 1L;
        ProductOption option1 = new ProductOption(1L, productId, "옵션1", ProductOptionStatus.ON_SALE, 10000L, 10, LocalDateTime.now(), null);
        ProductOption option2 = new ProductOption(2L, productId, "옵션2", ProductOptionStatus.ON_SALE, 15000L, 5, LocalDateTime.now(), null);

        when(productOptionRepository.findOptionsByProductId(productId)).thenReturn(List.of(option1, option2));

        // when
        List<ProductOptionDto> result = findProductOptionsService.findByProductId(productId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("optionId").containsExactly(1L, 2L);
        verify(productOptionRepository, times(1)).findOptionsByProductId(productId);
    }

    @Test
    @DisplayName("상품 ID로 조회 결과가 비어 있는 경우")
    void findByProductId_empty() {
        // given
        long productId = 99L;
        when(productOptionRepository.findOptionsByProductId(productId)).thenReturn(List.of());

        // when
        List<ProductOptionDto> result = findProductOptionsService.findByProductId(productId);

        // then
        assertThat(result).isEmpty();
        verify(productOptionRepository, times(1)).findOptionsByProductId(productId);
    }
}