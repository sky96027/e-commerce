package kr.hhplus.be.server.common.redis.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
@EnableCaching
public class CacheConfig {

    private static final String GLOBAL_PREFIX = "cache:v1:"; // 전역 버전 프리픽스

    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory cf) {
        // Jackson ObjectMapper 설정 - LocalDateTime 지원
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        var valueSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        RedisCacheConfiguration base = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext
                        .SerializationPair
                        .fromSerializer(valueSerializer))
                // cacheName 기반 프리픽스: cache:v1:{cacheName}: 로 저장
                .computePrefixWith(name -> GLOBAL_PREFIX + name + ":")
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> conf = new HashMap<>();
        
        // 상품 관련 - 변경 빈도 고려
        conf.put("product:detail", base.entryTtl(Duration.ofMinutes(60)));      // 1시간 (변경 빈도 낮음)
        conf.put("product:options", base.entryTtl(Duration.ofMinutes(10)));     // 10분 (재고 변경 빈도 높음)
        
        // 주문 관련 - 상태 변경 빈도 고려
        conf.put("order:summary", base.entryTtl(Duration.ofMinutes(2)));        // 2분 (주문 상태 변경 빈도 높음)
        
        // 쿠폰 관련 - 사용 패턴 고려
        conf.put("coupon:policy", base.entryTtl(Duration.ofMinutes(60)));       // 1시간 (정책 변경 빈도 낮음)
        conf.put("coupon:userSummary", base.entryTtl(Duration.ofMinutes(5)));   // 5분 (사용자 쿠폰 변경 중간)
        
        // 인기 상품 - 업데이트 주기 고려
        conf.put("popular:top", base.entryTtl(Duration.ofHours(24)));           // 24시간 (인기도 변경 빈도 매우 낮음)
        
        // 거래 내역 - 저장 빈도 고려
        conf.put("tx:recent", base.entryTtl(Duration.ofMinutes(1)));            // 1분 (거래 발생 빈도 높음)

        return RedisCacheManager.builder(cf)
                .cacheDefaults(base)
                .withInitialCacheConfigurations(conf)
                .transactionAware()
                .build();
    }
}