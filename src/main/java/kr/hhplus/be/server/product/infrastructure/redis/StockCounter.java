package kr.hhplus.be.server.product.infrastructure.redis;

import java.util.Collections;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

/**
 * Redis를 1차 소스로 사용하는 재고 카운터
 */
@Component
@RequiredArgsConstructor
public class StockCounter {

    private final StringRedisTemplate redis;

    // 해시 모드
    @Qualifier("atomicHashDecrementIfEnoughScript")
    private final RedisScript<Long> atomicHashDecrementIfEnoughScript;

    // === 해시 모드 API ===
    private String hashKey(long productId) {
        return "stock:prod:" + productId;
    }

    public void initStockHash(long productId, long optionId, long qty) {
        redis.opsForHash().put(hashKey(productId), String.valueOf(optionId), String.valueOf(qty));
    }

    public long getStockHash(long productId, long optionId) {
        Object v = redis.opsForHash().get(hashKey(productId), String.valueOf(optionId));
        return v == null ? 0L : Long.parseLong(v.toString());
    }

    /**
     * 원자 감소 (성공: 남은 수량 반환, 실패: -1)
     */
    public long tryDeductHash(long productId, long optionId, int qty) {
        Long r = redis.execute(
                atomicHashDecrementIfEnoughScript,
                Collections.singletonList(hashKey(productId)),
                String.valueOf(optionId),
                String.valueOf(qty)
        );
        return r == null ? -1 : r;
    }

    /**
     * 보상 복구(+qty)
     */
    public void compensateHash(long productId, long optionId, int qty) {
        redis.opsForHash().increment(hashKey(productId), String.valueOf(optionId), qty);
    }

    // === 단일 키 모드 API (선택) ===
    private String singleKey(long optionId) {
        return "stock:" + optionId;
    }

    public void initStockKey(long optionId, long qty) {
        redis.opsForValue().set(singleKey(optionId), String.valueOf(qty));
    }

    public long getStockKey(long optionId) {
        String v = redis.opsForValue().get(singleKey(optionId));
        return v == null ? 0L : Long.parseLong(v);
    }

    public long tryDeductKey(long optionId, int qty) {
        Long r = redis.execute(
                atomicHashDecrementIfEnoughScript,
                Collections.singletonList(singleKey(optionId)),
                String.valueOf(qty)
        );
        return r == null ? -1 : r;
    }

    public void compensateKey(long optionId, int qty) {
        redis.opsForValue().increment(singleKey(optionId), qty);
    }
}
