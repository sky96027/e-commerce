package kr.hhplus.be.server.user.application.mapper;

import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.infrastructure.entity.ProductJpaEntity;
import kr.hhplus.be.server.user.domain.model.User;
import org.springframework.stereotype.Component;

/**
 * [Mapper]
 * 도메인 모델로 변환하는 매퍼 클래스.
 *
 * 이 매퍼는 인프라 계층의 JPA Entity를 도메인 계층의 모델로 변환하여
 * application 계층에서 infra 의존성을 제거하는 역할을 한다.
 */
@Component
public class UserDtoMapper {

    public User toDomain(User user) {
        return new User(
                user.getUserId(),
                user.getBalance()
        );
    }
}
