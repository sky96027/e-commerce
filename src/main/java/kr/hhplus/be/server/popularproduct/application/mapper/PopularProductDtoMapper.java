package kr.hhplus.be.server.popularproduct.application.mapper;

import kr.hhplus.be.server.popularproduct.domain.model.PopularProduct;
import kr.hhplus.be.server.popularproduct.infrastructure.entity.PopularProductJpaEntity;
import org.springframework.stereotype.Component;

/**
 * [Mapper]
 * 도메인 모델로 변환하는 매퍼 클래스.
 *
 * 이 매퍼는 인프라 계층의 JPA Entity를 도메인 계층의 모델로 변환하여
 * application 계층에서 infra 의존성을 제거하는 역할을 한다.
 */
@Component
public class PopularProductDtoMapper {

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
}
