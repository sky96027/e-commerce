package kr.hhplus.be.server.user.infrastructure.repository;

import kr.hhplus.be.server.user.domain.model.User;
import kr.hhplus.be.server.user.domain.repository.UserRepository;
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
    private final Map<Long, User> table = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public User selectById(long userId) {
        throttle(200);
        return table.get(userId);
    }

    @Override
    public User insertOrUpdate(long userId, long amount) {
        throttle(200);
        long newId = idGenerator.getAndIncrement();
        User user = new User(userId, amount);
        table.put(newId, user);
        return user;
    }

    private void throttle(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep((long) (Math.random() * millis));
        } catch (InterruptedException ignored) {

        }
    }

}
