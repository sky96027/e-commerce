package kr.hhplus.be.server.product.application.mapper;

import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.infrastructure.entity.ProductJpaEntity;
import org.springframework.stereotype.Component;

/**
 * [Mapper]
 * 도메인 모델로 변환하는 매퍼 클래스.
 *
 * 이 매퍼는 인프라 계층의 JPA Entity를 도메인 계층의 모델로 변환하여
 * application 계층에서 infra 의존성을 제거하는 역할을 한다.
 */
@Component
public class ProductDtoMapper {

    public Product toDomain(ProductJpaEntity entity) {
        return new Product(
                entity.getProductId(),
                entity.getProductName(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getExpiredAt()
        );
    }
}