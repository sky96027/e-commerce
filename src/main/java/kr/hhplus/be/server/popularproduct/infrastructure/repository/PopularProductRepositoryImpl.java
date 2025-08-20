package kr.hhplus.be.server.popularproduct.infrastructure.repository;

import kr.hhplus.be.server.popularproduct.domain.model.PopularProduct;
import kr.hhplus.be.server.popularproduct.domain.repository.PopularProductRepository;
import kr.hhplus.be.server.popularproduct.infrastructure.entity.PopularProductJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Redis ZSet으로 구성됨.
 */
@Repository
@RequiredArgsConstructor
public class PopularProductRepositoryImpl implements PopularProductRepository {

    private final StringRedisTemplate redis;
    private final PopularProductJpaRepository jpaRepository; // 스냅샷 용(선택)

    private static final String RANK_KEY = "pop:rank:24h";

    @Override
    public List<PopularProduct> findAllSummaries() {
        int limit = 100;
        var tuples = redis.opsForZSet().reverseRangeWithScores(RANK_KEY, 0, limit - 1);
        if (tuples == null || tuples.isEmpty()) return List.of();

        var list = new ArrayList<PopularProduct>(tuples.size());
        int rank = 1;
        for (var t : tuples) {
            String member = t.getValue();                 // "product:{id}"
            long productId = Long.parseLong(member.substring("product:".length()));
            int sold = (int) Math.round(Optional.ofNullable(t.getScore()).orElse(0.0));

            list.add(new PopularProduct(
                    0L,                              // id (스냅샷 미사용)
                    productId,
                    sold,                            // totalSoldQuantity = score
                    rank++,                          // rank
                    java.time.LocalDate.now(),
                    java.time.LocalDateTime.now()
            ));
        }
        return list;
    }

    @Override
    public void replaceAll() {
        var tuples = redis.opsForZSet().reverseRangeWithScores(RANK_KEY, 0, 999);
        jpaRepository.deleteAllInBatch();
        if (tuples == null || tuples.isEmpty()) return;

        int rank = 1;
        var entities = new ArrayList<PopularProductJpaEntity>(tuples.size());
        for (var t : tuples) {
            long productId = Long.parseLong(t.getValue().substring("product:".length()));
            int sold = (int) Math.round(Optional.ofNullable(t.getScore()).orElse(0.0));

            var e = new PopularProductJpaEntity(
                null,                       // id
                productId,                  // productId
                sold,                       // totalSoldQuantity
                rank++,                     // rank
                LocalDate.now(),            // referenceDate
                LocalDateTime.now()         // createdAt
            );
            entities.add(e);
        }
        jpaRepository.saveAll(entities);
    }
}
