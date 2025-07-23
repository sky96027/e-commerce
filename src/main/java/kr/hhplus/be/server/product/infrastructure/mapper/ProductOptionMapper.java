package kr.hhplus.be.server.product.infrastructure.mapper;

import kr.hhplus.be.server.product.domain.model.ProductOption;
import kr.hhplus.be.server.product.infrastructure.entity.ProductOptionJpaEntity;
import org.springframework.stereotype.Component;

/**
 * ProductOptionJpaEntity ↔ ProductOption 변환 매퍼
 */
@Component
public class ProductOptionMapper {

    public ProductOption toDomain(ProductOptionJpaEntity entity) {
        return new ProductOption(
                entity.getOptionId(),
                entity.getProductId(),
                entity.getContent(),
                entity.getStatus(),
                entity.getPrice(),
                entity.getStock(),
                entity.getCreatedAt(),
                entity.getExpiredAt()
        );
    }

    public ProductOptionJpaEntity toEntity(ProductOption domain) {
        return new ProductOptionJpaEntity(
                domain.getOptionId(),
                domain.getProductId(),
                domain.getContent(),
                domain.getStatus(),
                domain.getPrice(),
                domain.getStock(),
                domain.getCreatedAt(),
                domain.getExpiredAt()
        );
    }
}