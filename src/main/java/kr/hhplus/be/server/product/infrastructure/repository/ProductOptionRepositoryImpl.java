package kr.hhplus.be.server.product.infrastructure.repository;

import kr.hhplus.be.server.product.domain.model.ProductOption;
import kr.hhplus.be.server.product.domain.repository.ProductOptionRepository;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory ProductOptionRepository 구현체
 */
@Repository
public class ProductOptionRepositoryImpl implements ProductOptionRepository {
    private final Map<Long, ProductOption> table = new HashMap<>();
    private final Map<Long, List<ProductOption>> optionListTable = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public List<ProductOption> findByProductId(long productId) {
        throttle(200);
        return optionListTable.getOrDefault(productId, Collections.emptyList());
    }

    @Override
    public ProductOption findByOptionId(long optionId) {
        throttle(200);
        return table.get(optionId);
    }

    @Override
    public void insertOrUpdate(ProductOption productOption) {
        throttle(200);
        long newId = idGenerator.getAndIncrement();
        table.put(newId, productOption);
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