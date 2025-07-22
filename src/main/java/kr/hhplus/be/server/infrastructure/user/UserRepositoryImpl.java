package kr.hhplus.be.server.infrastructure.user;

import kr.hhplus.be.server.domain.user.UserEntity;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * In-memory UserRepository 구현체
 */
@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, UserEntity> table = new HashMap<>();

    @Override
    public UserEntity selectById(long userId) {
        throttle(200);
        return table.getOrDefault(userId, UserEntity.empty(userId));
    }

    @Override
    public UserEntity insertOrUpdate(long userId, long amount) {
        throttle(200);
        UserEntity userEntity = new UserEntity(userId, amount);
        table.put(userId, userEntity);
        return userEntity;
    }

    private void throttle(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep((long) (Math.random() * millis));
        } catch (InterruptedException ignored) {

        }
    }

}
