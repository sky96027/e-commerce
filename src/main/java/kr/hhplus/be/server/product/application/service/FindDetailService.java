package kr.hhplus.be.server.product.application.service;

import kr.hhplus.be.server.common.redis.cache.CacheKeyUtil;
import kr.hhplus.be.server.product.application.dto.ProductDto;
import kr.hhplus.be.server.product.application.usecase.FindDetailUseCase;
import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * [UseCase 구현체]
 * FindDetailUseCase 인터페이스를 구현한 클래스.
 *
 * 도메인 계층의 ProductRepository를 사용하여 상품 데이터를 조회하고,
 * 그 결과를 ProductDto로 변환하여 반환한다.
 *
 * 이 클래스는 오직 "상품 상세 조회"라는 하나의 유스케이스만 책임지며,
 * 단일 책임 원칙(SRP)을 따르는 구조로 확장성과 테스트 용이성을 높인다.
 */
@Service
@RequiredArgsConstructor
public class FindDetailService implements FindDetailUseCase {

    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 상품 ID를 기반으로 상세 정보를 조회하고 DTO로 변환한다.
     * @param productId 상품 ID
     * @return 상품 정보 DTO
     */
    @Override
    public ProductDto findById(long productId) {
        String key = CacheKeyUtil.productDetailKey(productId);

        ProductDto cached = (ProductDto) redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return cached;
        }

        Product product = productRepository.findById(productId);
        ProductDto dto = ProductDto.from(product);

        redisTemplate.opsForValue().set(key, dto, Duration.ofHours(1));

        return dto;
    }
}