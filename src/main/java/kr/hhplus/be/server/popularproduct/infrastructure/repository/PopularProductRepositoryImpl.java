package kr.hhplus.be.server.popularproduct.infrastructure.repository;

import kr.hhplus.be.server.popularproduct.domain.model.PopularProduct;
import kr.hhplus.be.server.popularproduct.domain.repository.PopularProductRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * In-memory 구현체
 */
@Repository
public class PopularProductRepositoryImpl implements PopularProductRepository {
    private final Map<Long, PopularProduct> table = new HashMap<>();

    @Override
    public List<PopularProduct> selectSummaries() {
        throttle(200);
        return new ArrayList<>(table.values());
    }

    @Override
    public void replaceAll() {
        throttle(200);

        // 전체 삭제, JPA 도입 시 변경
        table.clear();

    }

    private void throttle(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep((long) (Math.random() * millis));
        } catch (InterruptedException ignored) {
        }
    }
}
