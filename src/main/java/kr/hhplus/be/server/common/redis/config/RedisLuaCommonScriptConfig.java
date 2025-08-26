package kr.hhplus.be.server.common.redis.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

/**
 * 공용 Redis Lua 스크립트 설정
 * - atomic-hash-decrement
 * - atomic-key-decrement
 */
@Configuration
public class RedisLuaCommonScriptConfig {

    /** 해시 기반 원자적 감소: KEYS[1]=stock:product:{productId}, ARGV[1]=field(optionId 등), ARGV[2]=qty */
    @Bean
    @Primary
    public RedisScript<Long> atomicHashDecrementIfEnoughScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("redis/scripts/atomic-hash-decrement.lua"));
        script.setResultType(Long.class);
        return script;
    }
}
