package kr.hhplus.be.server.product.infrastructure.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.script.DefaultRedisScript;

/**
 * 재고(HASH/KEY) 원자 감소용 Lua 스크립트 빈들
 */
@Configuration
public class StockLuaScriptsConfig {

    /**
     * 해시 기반: KEYS[1]=stock:prod:{productId}, ARGV[1]=optionId, ARGV[2]=qty
     * cur < req 이면 -1, 성공 시 감소 후 남은 수량 반환
     */
    @Bean
    public DefaultRedisScript<Long> stockDecrementHashScript() {
        var s = new DefaultRedisScript<Long>();
        s.setResultType(Long.class);
        s.setScriptText("""
            local cur = tonumber(redis.call('HGET', KEYS[1], ARGV[1]) or '0')
            local req = tonumber(ARGV[2])
            if cur < req then return -1 end
            return redis.call('HINCRBY', KEYS[1], ARGV[1], -req)
        """);
        return s;
    }

    /**
     * 단일 키 기반: KEYS[1]=stock:{optionId}, ARGV[1]=qty
     * cur < req 이면 -1, 성공 시 감소 후 남은 수량 반환
     */
    @Bean
    public DefaultRedisScript<Long> stockDecrementKeyScript() {
        var s = new DefaultRedisScript<Long>();
        s.setResultType(Long.class);
        s.setScriptText("""
            local cur = tonumber(redis.call('GET', KEYS[1]) or '0')
            local req = tonumber(ARGV[1])
            if cur < req then return -1 end
            return redis.call('DECRBY', KEYS[1], req)
        """);
        return s;
    }
}