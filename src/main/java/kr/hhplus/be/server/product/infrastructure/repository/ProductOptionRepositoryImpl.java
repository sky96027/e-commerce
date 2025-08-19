package kr.hhplus.be.server.product.infrastructure.repository;

import kr.hhplus.be.server.common.exception.RestApiException;
import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.domain.model.ProductOption;
import kr.hhplus.be.server.product.domain.repository.ProductOptionRepository;
import kr.hhplus.be.server.product.exception.ProductErrorCode;
import kr.hhplus.be.server.product.infrastructure.entity.ProductOptionJpaEntity;
import kr.hhplus.be.server.product.infrastructure.mapper.ProductOptionMapper;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * In-memory ProductOptionRepository 구현체
 */
@Repository
public class ProductOptionRepositoryImpl implements ProductOptionRepository {

    private final ProductOptionJpaRepository jpaRepository;
    private final ProductOptionMapper mapper;

    public ProductOptionRepositoryImpl(ProductOptionJpaRepository jpaRepository, ProductOptionMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<ProductOption> findOptionsByProductId(long productId) {
        return jpaRepository.findOptionsByProductId(productId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public ProductOption findOptionByOptionId(long optionId) {
        return jpaRepository.findById(optionId)
                .map(mapper::toDomain)
                .orElseThrow(()->
                        new RestApiException(ProductErrorCode.OPTION_NOT_FOUND_ERROR));
    }

    @Override
    public ProductOption insertOrUpdate(ProductOption productOption) {
        ProductOptionJpaEntity entity = mapper.toEntity(productOption);
        ProductOptionJpaEntity saved = jpaRepository.saveAndFlush(entity); // ← flush 보장
        return mapper.toDomain(saved);
    }

    @Override
    public void decrementStock(long optionId, int quantity) {
        if (quantity <= 0) {
            throw new RestApiException(ProductErrorCode.INVALID_QUANTITY_ERROR);
        }

        int updated = jpaRepository.decrementStockIfEnough(optionId, quantity);
        if (updated != 1) {
            throw new RestApiException(ProductErrorCode.OUT_OF_STOCK_ERROR);
        }
    }

    @Override
    public void  incrementStock(long optionId, int quantity) {
        if (quantity <= 0) {
            throw new RestApiException(ProductErrorCode.INVALID_QUANTITY_ERROR);
        }

        int updated = jpaRepository.incrementStock(optionId, quantity);
    }

    // 비관적 Lock (Legacy)
    /*@Override
    public ProductOption findOptionByOptionIdForUpdate(long optionId) {
        return jpaRepository.findByIdForUpdate(optionId)
                .map(mapper::toDomain)
                .orElse(null);
    }*/
}