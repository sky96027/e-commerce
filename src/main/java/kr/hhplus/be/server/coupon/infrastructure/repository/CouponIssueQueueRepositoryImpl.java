package kr.hhplus.be.server.coupon.infrastructure.repository;

import kr.hhplus.be.server.coupon.domain.repository.CouponIssueQueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class CouponIssueQueueRepositoryImpl implements CouponIssueQueueRepository {

    private final StringRedisTemplate redis;
    private final @Qualifier("enqueueLua") DefaultRedisScript<Long> enqueueLua;
    private final @Qualifier("popIfPayloadExistsLua") DefaultRedisScript<String> popIfPayloadExistsLua;

    private String qKey(long couponId)  { return "coupon:issue:{" + couponId + "}:q"; }
    private String seqKey(long couponId) { return "coupon:issue:{" + couponId + "}:seq"; }
    private String payloadPrefix()       { return "coupon:issue:cmd:"; }

    @Override
    public long enqueue(long couponId, long userId, String reservationId) {
        String member = reservationId + ":" + userId;
        Long score = redis.execute(
                enqueueLua,
                List.of(qKey(couponId), seqKey(couponId)),
                member
        );
        return score == null ? -1L : score;
    }


    @Override
    public String popNext(long couponId) {
        ZSetOperations.TypedTuple<String> tuple = redis.opsForZSet().popMin(qKey(couponId));
        return (tuple == null) ? null : tuple.getValue();
    }

    @Override
    public long size(long couponId) {
        Long len = redis.opsForZSet().zCard(qKey(couponId));
        return len == null ? 0L : len;
    }

    @Override
    public PopResult popNextSafe(long couponId) {
        String res = redis.execute(
                popIfPayloadExistsLua,
                List.of(qKey(couponId)),
                payloadPrefix() // ARGV[1]
        );

        if (res == null || "EMPTY".equals(res)) return PopResult.empty();
        if (res.startsWith("OK:"))      return PopResult.ok(res.substring(3));
        if (res.startsWith("MISSING:")) return PopResult.missing(res.substring(8));
        // 예외 케이스 방어
        return PopResult.missing(res);
    }
}
