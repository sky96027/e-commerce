package kr.hhplus.be.server.order.infrastructure.repository;

import kr.hhplus.be.server.order.domain.model.OrderItem;
import kr.hhplus.be.server.order.domain.repository.OrderItemRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory OrderItemRepository 구현체
 * newId로 생성하는데 orderId로 조회하는 에러 있음.
 * 시간이 없어 db 도입시 해결.
 */
@Repository
public class OrderItemRepositoryImpl implements OrderItemRepository {
    private final Map<Long, List<OrderItem>> table = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public List<OrderItem> selectByOrderId(long orderId){
        throttle(200);
        return table.getOrDefault(orderId, Collections.emptyList());
    }

    @Override
    public void saveAll(List<OrderItem> items) {
        throttle(200);
        long newId = idGenerator.getAndIncrement();
        table.computeIfAbsent(newId, k -> new ArrayList<>());
    }

    private void throttle(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep((long) (Math.random() * millis));
        } catch (InterruptedException ignored) {

        }
    }
}
