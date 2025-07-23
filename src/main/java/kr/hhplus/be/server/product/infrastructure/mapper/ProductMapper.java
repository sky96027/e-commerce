package kr.hhplus.be.server.product.infrastructure.mapper;

import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.infrastructure.entity.ProductJpaEntity;
import org.springframework.stereotype.Component;

/**
 * ProductJpaEntity ↔ Product 변환 매퍼
 */
@Component
public class ProductMapper {

    public Product toDomain(ProductJpaEntity entity) {
        return new Product(
                entity.getProductId(),
                entity.getProductName(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getExpiredAt()
        );
    }

    public ProductJpaEntity toEntity(Product domain) {
        return new ProductJpaEntity(
                domain.getProductId(),
                domain.getProductName(),
                domain.getStatus(),
                domain.getCreatedAt(),
                domain.getExpiredAt()
        );
    }
}