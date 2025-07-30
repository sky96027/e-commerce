package kr.hhplus.be.server.user.infrastructure.repository;

import kr.hhplus.be.server.user.domain.model.User;
import kr.hhplus.be.server.user.domain.repository.UserRepository;
import kr.hhplus.be.server.user.infrastructure.entity.UserJpaEntity;
import kr.hhplus.be.server.user.infrastructure.mapper.UserMapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory UserRepository 구현체
 */
@Repository
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository jpaRepository;
    private final UserMapper mapper;

    public UserRepositoryImpl(UserJpaRepository userJpaRepository, UserMapper mapper) {
        this.jpaRepository = userJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public User selectById(long userId) {
        return jpaRepository.findById(userId)
                .map(mapper::toDomain)
                .orElse(null);
    }

    @Override
    public User insert(long balance) {
        UserJpaEntity newEntity = new UserJpaEntity(balance);  // 생성자 수정 필요
        UserJpaEntity saved = jpaRepository.save(newEntity);
        return mapper.toDomain(saved);
    }

    @Override
    public User update(long userId, long balance) {
        UserJpaEntity entity = jpaRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다: " + userId));
        entity.setBalance(balance);
        return mapper.toDomain(jpaRepository.save(entity));
    }
}
