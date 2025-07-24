package kr.hhplus.be.server.order.infrastructure.repository;

import kr.hhplus.be.server.order.domain.model.Order;
import kr.hhplus.be.server.order.domain.repository.OrderRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory OrderRepository 구현체
 */
@Repository
public class OrderRepositoryImpl implements OrderRepository {
    private final Map<Long, Order> table = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Order selectByOrderId (long orderId) {
        throttle(200);
        return table.get(orderId);
    }

    @Override
    public void save(Order order) {
        throttle(200);
        long newId = idGenerator.getAndIncrement();
        table.put(newId, order);
    }

    private void throttle(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep((long) (Math.random() * millis));
        } catch (InterruptedException ignored) {

        }
    }
}
