package com.kkimleang.rrms.config;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.lang.NonNull;
import redis.clients.jedis.Jedis;

@AutoConfigureAfter(RedisAutoConfiguration.class)
@Configuration
public class RedisCacheConfig implements CachingConfigurer {
    @Value("${spring.data.redis.host}")
    private String redisHost;
    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    @Override
    public CacheErrorHandler errorHandler() {
        return new DefaultCacheErrorHandler();
    }

    @PostConstruct
    public void clearCache() {
        System.out.println("In Clear Cache");
        Jedis jedis = new Jedis(redisHost, redisPort, 1000);
        jedis.flushAll();
        jedis.close();
    }
}

class DefaultCacheErrorHandler extends SimpleCacheErrorHandler {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultCacheErrorHandler.class);

    @Override
    public void handleCacheGetError(
            @NonNull RuntimeException exception,
            @NonNull Cache cache,
            @NonNull Object key
    ) {
        LOG.info(
                "handleCacheGetError ~ {}: {} - {}",
                exception.getMessage(),
                cache.getName(),
                key
        );
    }

    @Override
    public void handleCachePutError(
            @NonNull RuntimeException exception,
            @NonNull Cache cache,
            @NonNull Object key,
            Object value
    ) {
        LOG.info(
                "handleCachePutError ~ {}: {} - {}",
                exception.getMessage(),
                cache.getName(),
                key
        );
        super.handleCachePutError(exception, cache, key, value);
    }

    @Override
    public void handleCacheEvictError(
            @NonNull RuntimeException exception,
            @NonNull Cache cache,
            @NonNull Object key
    ) {
        LOG.info(
                "handleCacheEvictError ~ {}: {} - {}",
                exception.getMessage(),
                cache.getName(),
                key
        );
        super.handleCacheEvictError(exception, cache, key);
    }

    @Override
    public void handleCacheClearError(
            @NotNull RuntimeException exception,
            @NotNull Cache cache
    ) {
        LOG.info(
                "handleCacheClearError ~ {}: {}",
                exception.getMessage(),
                cache.getName()
        );
        super.handleCacheClearError(exception, cache);
    }
}


