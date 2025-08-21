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

    /** 해시 기반 원자적 감소: KEYS[1]=stock:prod:{productId}, ARGV[1]=field(optionId 등), ARGV[2]=qty */
    // Primary는 atomicHashDecrementIfEnoughScript가 어디서 중복생성되는지 찾은 후에 제거
    // Primary 사용은 최대한 지양해야함
    @Bean
    @Primary
    public RedisScript<Long> atomicHashDecrementIfEnoughScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("redis/scripts/atomic-hash-decrement.lua"));
        script.setResultType(Long.class);
        return script;
    }



    /** 단일 키 기반 원자적 감소: KEYS[1]=stock:{optionId}, ARGV[1]=qty */
    @Bean
    public RedisScript<Long> atomicKeyDecrementIfEnoughScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("redis/scripts/atomic-key-decrement.lua"));
        script.setResultType(Long.class);
        return script;
    }
}
