package kr.hhplus.be.server.product.infrastructure.repository;

import kr.hhplus.be.server.product.domain.model.ProductOption;
import kr.hhplus.be.server.product.domain.repository.ProductOptionRepository;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * In-memory ProductOptionRepository 구현체
 */
@Repository
public class ProductOptionRepositoryImpl implements ProductOptionRepository {
    private final Map<Long, List<ProductOption>> table = new HashMap<>();

    @Override
    public List<ProductOption> findByProductId(long productId) {
        throttle(200);
        return table.getOrDefault(productId, Collections.emptyList());
    }

    private void throttle(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep((long) (Math.random() * millis));
        } catch (InterruptedException ignored) {
        }
    }

    //db 도입시 아래 사용
    /*private final ProductOptionJpaRepository jpaRepository;
    private final ProductOptionEntityMapper mapper;

    public ProductOptionRepositoryImpl(ProductOptionJpaRepository jpaRepository, ProductOptionEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<ProductOption> findByProductId(long productId) {
        return jpaRepository.findByProductId(productId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }*/
}