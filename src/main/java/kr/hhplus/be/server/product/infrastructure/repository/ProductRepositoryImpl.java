package kr.hhplus.be.server.product.infrastructure.repository;

import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.infrastructure.entity.ProductJpaEntity;
import kr.hhplus.be.server.product.domain.repository.ProductRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * In-memory ProductRepository 구현체
 */
@Repository
public class ProductRepositoryImpl implements ProductRepository {
    private final Map<Long, Product> table = new HashMap<>();

    @Override
    public Product selectById(long productId) {
        throttle(200);
        return table.get(productId);
    }

    @Override
    public List<Product> selectSummaries() {
        throttle(200);
        return new ArrayList<>(table.values());
    }

    private void throttle(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep((long) (Math.random() * millis));
        } catch (InterruptedException ignored) {
        }
    }



}