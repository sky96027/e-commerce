package kr.hhplus.be.server.common.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import kr.hhplus.be.server.transactionhistory.application.usecase.FindHistoryUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

@Slf4j
@Configuration
@EnableCaching(proxyTargetClass = true)
public class CacheConfig {

    private static final String GLOBAL_PREFIX = "cache:v1:"; // 전역 버전 프리픽스

    @Bean
    @Primary
    public CacheManager redisCacheManager(RedisConnectionFactory cf) {
        log.info("=== Redis 캐시 매니저 생성 시작 ===");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        objectMapper.activateDefaultTyping(
            LaissezFaireSubTypeValidator.instance,
            ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.PROPERTY
        );

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        objectMapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
        
        log.info("=== Jackson ObjectMapper 설정 완료 ===");
        
        var valueSerializer = new GenericJackson2JsonRedisSerializer(objectMapper) {
            @Override
            public Object deserialize(byte[] bytes) throws SerializationException {
                try {
                    return super.deserialize(bytes);
                } catch (Exception e) {
                    // 역직렬화 실패 시 null 반환하여 캐시 미스 처리
                    return null;
                }
            }
        };
        var keySerializer = new StringRedisSerializer();
        
        log.info("=== 직렬화기 설정 완료 ===");
        
        RedisCacheConfiguration base = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext
                        .SerializationPair
                        .fromSerializer(keySerializer))
                .serializeValuesWith(RedisSerializationContext
                        .SerializationPair
                        .fromSerializer(valueSerializer))
                // cacheName 기반 프리픽스: cache:v1:{cacheName}: 로 저장
                .computePrefixWith(name -> GLOBAL_PREFIX + name + ":")
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues();

        log.info("=== 기본 캐시 설정 완료 - 프리픽스: {} ===", GLOBAL_PREFIX);

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
        conf.put("tx:recent", base.entryTtl(Duration.ofMinutes(5)));           // 5분 (거래 발생 빈도 높음, 캐시 문제 최소화)

        log.info("=== 캐시별 TTL 설정 완료 ===");

        CacheManager cacheManager = RedisCacheManager.builder(cf)
                .cacheDefaults(base)
                .withInitialCacheConfigurations(conf)
                .transactionAware()
                .build();
                
        log.info("=== Redis 캐시 매니저 생성 완료 ===");
        
        return cacheManager;
    }

    @Bean
    public CacheErrorHandler cacheErrorHandler() {
        return new SimpleCacheErrorHandler() {

            @Override
            public void handleCachePutError(RuntimeException ex,
                                            org.springframework.cache.Cache cache,
                                            Object key, Object value) {
                log.error("[CACHE PUT ERROR] cache={}, key={}, valueType={}, err={}",
                        cache != null ? cache.getName() : "null",
                        key,
                        value != null ? value.getClass().getName() : "null",
                        ex.toString(), ex);
                // 필요하면 super 호출 유지(그대로 예외 전파)
                super.handleCachePutError(ex, cache, key, value);
            }

            @Override
            public void handleCacheGetError(RuntimeException ex,
                                            org.springframework.cache.Cache cache,
                                            Object key) {
                log.error("[CACHE GET ERROR] cache={}, key={}, err={}",
                        cache != null ? cache.getName() : "null",
                        key, ex.toString(), ex);
                super.handleCacheGetError(ex, cache, key);
            }

            @Override
            public void handleCacheEvictError(RuntimeException ex,
                                              org.springframework.cache.Cache cache,
                                              Object key) {
                log.error("[CACHE EVICT ERROR] cache={}, key={}, err={}",
                        cache != null ? cache.getName() : "null",
                        key, ex.toString(), ex);
                super.handleCacheEvictError(ex, cache, key);
            }

            @Override
            public void handleCacheClearError(RuntimeException ex,
                                              org.springframework.cache.Cache cache) {
                log.error("[CACHE CLEAR ERROR] cache={}, err={}",
                        cache != null ? cache.getName() : "null",
                        ex.toString(), ex);
                super.handleCacheClearError(ex, cache);
            }
        };
    }

    @Bean
    public ApplicationRunner cacheBootLog(CacheManager cm, ApplicationContext ctx) {
        return args -> {
            log.info("[CACHE] CacheManager = {}", cm.getClass().getName());
            log.info("[CACHE] tx:recent cache present = {}", cm.getCache("tx:recent") != null);
            Object bean = ctx.getBean(FindHistoryUseCase.class);
            log.info("[CACHE] FindHistoryUseCase bean class = {}", bean.getClass().getName());
        };
    }

}