package kr.hhplus.be.server.product.infrastructure.repository;

import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.domain.repository.ProductRepository;
import kr.hhplus.be.server.product.infrastructure.entity.ProductJpaEntity;
import kr.hhplus.be.server.product.infrastructure.mapper.ProductMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * In-memory ProductRepository 구현체
 */
@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository jpaRepository;
    private final ProductMapper mapper;

    public ProductRepositoryImpl(ProductJpaRepository jpaRepository, ProductMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Product findById(long productId) {
        return jpaRepository.findById(productId)
                .map(mapper::toDomain)
                .orElse(null);
    }

    @Override
    public List<Product> findAllSummaries() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Product insertOrUpdate(Product product) {
        ProductJpaEntity entity = mapper.toEntity(product);
        ProductJpaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
}