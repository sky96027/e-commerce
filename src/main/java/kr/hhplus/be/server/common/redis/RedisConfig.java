package kr.hhplus.be.server.common.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * Redis 연결 팩토리를 등록하고 스프링 캐시를 활성화하는 설정 클래스.
 *
 * - @Configuration: 스프링 설정 클래스임을 명시.
 * - @EnableCaching: @Cacheable, @CacheEvict 등 캐시 어노테이션을 사용할 수 있게 활성화.
 */
@Configuration
@EnableCaching
public class RedisConfig {
    /*@Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private String port;*/

    /**
     * Lettuce 기반 Redis 커넥션 팩토리 빈 등록.
     * - Lettuce는 Netty 기반 비동기/논블로킹 클라이언트로, 높은 동시성에 유리합니다.
     * - 이 빈을 통해 스프링 데이터 Redis 및 캐시 매니저가 Redis 서버와 통신합니다.
     * 자동 구성된 ConnectionFactory 사용으로 비활성화
     */
    /*@Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, Integer.parseInt(port));
    }*/

    /** 간단 문자열 기반 연산용 템플릿 (SET NX PX 등) */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
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

    /** Pub/Sub 락 해제 스크립트:
     * DEL + PUBLISH 원자화 스크립트
     * */
    @Bean
    public DefaultRedisScript<Long> unlockAndPublishScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setResultType(Long.class);
        script.setScriptText(
                // KEYS[1]=lockKey, KEYS[2]=channel, ARGV[1]=token
                "local v = redis.call('GET', KEYS[1]); " +
                        "if v == ARGV[1] then " +
                        "  redis.call('DEL', KEYS[1]); " +
                        "  redis.call('PUBLISH', KEYS[2], ARGV[1]); " +
                        "  return 1; " +
                        "else return 0; end"
        );
        return script;
    }

    /** Pub/Sub 구독 컨테이너 */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory cf) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(cf);
        return container;
    }
}
