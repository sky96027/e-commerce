package kr.hhplus.be.server.coupon.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;

/**
 * 쿠폰 도메인에서 사용하는 Redis Lua 스크립트 설정
 */
@Configuration
public class CouponRedisScriptConfig {

    /** 안전 pop: payload가 있으면 pop(OK), 없으면 tombstone pop(MISSING) */
    @Bean
    public DefaultRedisScript<String> popIfPayloadExistsLua() {
        DefaultRedisScript<String> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("redis/scripts/pop-if-payload-exists.lua"));
        script.setResultType(String.class);
        return script;
    }
}
