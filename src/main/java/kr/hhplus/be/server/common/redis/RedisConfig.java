package kr.hhplus.be.server.common.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.ReadFrom;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.protocol.ProtocolVersion;
import java.time.Duration;

/**
 * Redis 연결 팩토리를 등록하고 스프링 캐시를 활성화하는 설정 클래스.
 *
 * - @Configuration: 스프링 설정 클래스임을 명시.
 * - @EnableCaching은 CacheConfig에서만 활성화하여 중복 방지.
 */
@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.host:localhost}")
    private String host;

    @Value("${spring.data.redis.port:6379}")
    private int port;

    @Value("${spring.data.redis.timeout:2000}")
    private long timeout;

    /**
     * Lettuce 기반 Redis 커넥션 팩토리 빈 등록.
     * - Lettuce는 Netty 기반 비동기/논블로킹 클라이언트로, 높은 동시성에 유리합니다.
     * - 이 빈을 통해 스프링 데이터 Redis 및 캐시 매니저가 Redis 서버와 통신합니다.
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        
        // Lettuce 클라이언트 설정
        ClientOptions clientOptions = ClientOptions.builder()
                .timeoutOptions(TimeoutOptions.enabled(Duration.ofMillis(timeout)))
                .protocolVersion(ProtocolVersion.RESP3)
                .build();
        
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .clientOptions(clientOptions)
                .readFrom(ReadFrom.REPLICA_PREFERRED)
                .commandTimeout(Duration.ofMillis(timeout))
                .shutdownTimeout(Duration.ofSeconds(5))
                .build();
        
        return new LettuceConnectionFactory(config, clientConfig);
    }

    /** 간단 문자열 기반 연산용 템플릿 (SET NX PX 등) */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate template = new StringRedisTemplate(factory);
        template.setDefaultSerializer(new org.springframework.data.redis.serializer.StringRedisSerializer());
        return template;
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
