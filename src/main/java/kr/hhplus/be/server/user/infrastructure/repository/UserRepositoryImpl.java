package kr.hhplus.be.server.user.infrastructure.repository;

import kr.hhplus.be.server.user.domain.model.User;
import kr.hhplus.be.server.user.infrastructure.entity.UserJpaEntity;
import kr.hhplus.be.server.user.domain.repository.UserRepository;
import kr.hhplus.be.server.user.infrastructure.mapper.UserMapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * In-memory UserRepository 구현체
 */
@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, Long> table = new HashMap<>();
    private final UserMapper userMapper = new UserMapper();

    @Override
    public User selectById(long userId) {
        throttle(200);
        return User.empty(userId);
    }

    @Override
    public User insertOrUpdate(long userId, long amount) {
        throttle(200);
        User user = new User(userId, amount);
        table.put(userId, amount);
        return user;
    }

    private void throttle(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep((long) (Math.random() * millis));
        } catch (InterruptedException ignored) {

        }
    }

}
