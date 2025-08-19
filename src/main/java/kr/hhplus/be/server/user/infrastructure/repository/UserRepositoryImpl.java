package kr.hhplus.be.server.user.infrastructure.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.common.exception.RestApiException;
import kr.hhplus.be.server.user.domain.model.User;
import kr.hhplus.be.server.user.domain.repository.UserRepository;
import kr.hhplus.be.server.user.exception.UserErrorCode;
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
        if (balance < 0) throw new RestApiException(UserErrorCode.INVALID_INITIAL_BALANCE_ERROR);
        UserJpaEntity saved = jpaRepository.save(new UserJpaEntity(balance));
        return mapper.toDomain(saved);
    }

    @Override
    public void charge(long userId, long amount) {
        User.requirePositive(amount); // 입력 검증(도메인 규칙)
        int updated = jpaRepository.incrementBalance(userId, amount);
        if (updated != 1) {
            // 사용자 미존재 또는 동시성 이슈(비정상) — 일단 not found로 통일
            throw new RestApiException(UserErrorCode.USER_NOT_FOUND_ERROR);
        }
    }

    @Override
    public void deduct(long userId, long amount) {
        User.requirePositive(amount); // 입력 검증
        int updated = jpaRepository.decrementBalanceIfEnough(userId, amount);
        if (updated != 1) {
            // 사용자 미존재 또는 동시성 이슈(비정상) — 일단 not found로 통일
            throw new RestApiException(UserErrorCode.USER_NOT_FOUND_ERROR);
        }
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
