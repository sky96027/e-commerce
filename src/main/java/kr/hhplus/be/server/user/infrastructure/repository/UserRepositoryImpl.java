package kr.hhplus.be.server.user.infrastructure.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.user.domain.model.User;
import kr.hhplus.be.server.user.domain.repository.UserRepository;
import kr.hhplus.be.server.user.infrastructure.entity.UserJpaEntity;
import kr.hhplus.be.server.user.infrastructure.mapper.UserMapper;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
    public User findById(long userId) {
        return jpaRepository.findById(userId)
                .map(mapper::toDomain)
                .orElse(null);
    }

    @Override
    public User insert(long balance) {
        UserJpaEntity newEntity = new UserJpaEntity(balance);
        UserJpaEntity saved = jpaRepository.save(newEntity);
        return mapper.toDomain(saved);
    }

    @Override
    public User charge(long userId, long amount) {
        int updated = jpaRepository.incrementBalance(userId, amount);
        if (updated != 1) {
            throw new IllegalStateException("충전 실패: userId=" + userId);
        }
        UserJpaEntity entity = jpaRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found: " + userId));
        return mapper.toDomain(entity);
    }

    @Override
    public User deduct(long userId, long amount) {
        int updated = jpaRepository.decrementBalanceIfEnough(userId, amount);
        if (updated != 1) {
            throw new IllegalStateException("잔액 부족 또는 사용자 없음: userId=" + userId);
        }
        UserJpaEntity entity = jpaRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found: " + userId));
        return mapper.toDomain(entity);
    }


    /*@Override
    public User update(long userId, long balance) {
        UserJpaEntity entity = jpaRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다: " + userId));
        entity.setBalance(balance);
        return mapper.toDomain(jpaRepository.save(entity));
    }*/

    // 비관적 락 (Legacy)
    /*@Override
    public User selectByIdForUpdate(long userId) {
        return jpaRepository.findByIdForUpdate(userId)
                .map(mapper::toDomain)
                .orElse(null);
    }*/
}
