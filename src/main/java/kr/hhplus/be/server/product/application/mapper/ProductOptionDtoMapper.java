package kr.hhplus.be.server.product.application.mapper;

import kr.hhplus.be.server.product.domain.model.ProductOption;
import kr.hhplus.be.server.product.infrastructure.entity.ProductOptionJpaEntity;
import org.springframework.stereotype.Component;

/**
 * [Mapper]
 * 도메인 모델로 변환하는 매퍼 클래스.
 *
 * 이 매퍼는 JPA Entity를 도메인 모델로 변환함으로써
 * application 계층이 인프라 계층에 직접 의존하지 않도록 돕는다.
 */
@Component
public class ProductOptionDtoMapper {

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
}