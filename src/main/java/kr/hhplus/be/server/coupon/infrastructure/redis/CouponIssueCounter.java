package kr.hhplus.be.server.coupon.infrastructure.redis;

import java.util.Collections;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponIssueCounter {

    private final StringRedisTemplate redis;

    @Qualifier("atomicHashDecrementIfEnoughScript")
    private final RedisScript<Long> decScript;

    private String key(long couponId) {
        // 쿠폰별 재고 해시 키
        return "coupon:issue:%d:inv".formatted(couponId);
    }

    /** 초기 적재 또는 관리자 변경 시 사용 */
    public void init(long couponId, int remaining) {
        redis.opsForHash().put(key(couponId), "remaining", String.valueOf(remaining));
    }

    /** 없으면 -2(센티널) */
    public long getRemaining(long couponId) {
        Object v = redis.opsForHash().get(key(couponId), "remaining");
        return (v == null) ? -2L : Long.parseLong(v.toString());
    }

    /** 원자 감소(성공: 남은 수량, 부족: -1) */
    public long tryDecrement(long couponId, int qty) {
        Long r = redis.execute(
                decScript,
                Collections.singletonList(key(couponId)),
                "remaining",
                String.valueOf(qty)
        );
        return (r == null) ? -1L : r;
    }

    /** 보상 복구(+qty) */
    public void compensate(long couponId, int qty) {
        redis.opsForHash().increment(key(couponId), "remaining", qty);
    }
}
