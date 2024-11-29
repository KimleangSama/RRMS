package com.kkimleang.rrms.config;

import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;

@Configuration
public class RedisCacheConfig implements CachingConfigurer {
    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }

    @Bean
    @Override
    public CacheErrorHandler errorHandler() {
        return new DefaultCacheErrorHandler();
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


