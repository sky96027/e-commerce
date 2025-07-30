package kr.hhplus.be.server.user.infrastructure.mapper;

import kr.hhplus.be.server.user.domain.model.User;
import kr.hhplus.be.server.user.infrastructure.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Entity ↔ Domain model 변환 매퍼
 */
@Component
public class UserMapper {
    public User toDomain(UserJpaEntity entity) {
        return new User(
                entity.getUserId(),
                entity.getUserId()
        );
    }

    public UserJpaEntity toEntity(User domain) {
        return new UserJpaEntity(
                domain.getUserId(),
                domain.getBalance()
        );
    }
}
