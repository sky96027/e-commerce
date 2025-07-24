package kr.hhplus.be.server.popularproduct.infrastructure.mapper;

import kr.hhplus.be.server.popularproduct.domain.model.PopularProduct;
import kr.hhplus.be.server.popularproduct.infrastructure.entity.PopularProductJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Entity ↔ Domain Model 변환 매퍼
 */
@Component
public class PopularProductMapper {

    public PopularProduct toDomain(PopularProductJpaEntity entity) {
        return new PopularProduct(
                entity.getId(),
                entity.getProductId(),
                entity.getTotalSoldQuantity(),
                entity.getRank(),
                entity.getReferenceDate(),
                entity.getCreatedAt()
        );
    }

    public PopularProductJpaEntity toEntity(PopularProduct domain) {
        return new PopularProductJpaEntity(
                domain.getId(),
                domain.getProductId(),
                domain.getTotalSoldQuantity(),
                domain.getRank(),
                domain.getReferenceDate(),
                domain.getCreatedAt()
        );
    }
}
