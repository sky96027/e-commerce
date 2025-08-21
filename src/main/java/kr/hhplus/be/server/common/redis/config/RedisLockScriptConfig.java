package kr.hhplus.be.server.common.redis.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;

/**
 * Redis 락 관련 Lua 스크립트 설정
 */
@Configuration
public class RedisLockScriptConfig {

    /** Pub/Sub 락 해제 스크립트: DEL + PUBLISH 원자화 */
    @Bean
    public DefaultRedisScript<Long> unlockAndPublishScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("redis/scripts/unlock-and-publish.lua"));
        script.setResultType(Long.class);
        return script;
    }

    /**
     * Spin 락 해제 스크립트:
     * (1) 현재 키의 값이 전달받은 토큰과 같으면 DEL
     * (2) 아니면 아무 것도 하지 않음
     */
    /*@Bean
    public DefaultRedisScript<Long> unlockScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setResultType(Long.class);
        script.setScriptText(
                "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                        "  return redis.call('del', KEYS[1]) " +
                        "else " +
                        "  return 0 " +
                        "end"
        );
        return script;
    }*/
}
