package com.fox.api.configuration.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@EnableCaching
@ConfigurationProperties("spring.redis.stock")
public class StockRedisCacheConfig extends AbstractRedisCacheConfig {

    @Bean("stockRedisFactory")
    public LettuceConnectionFactory factory() {
        return super.factory();
    }

    @Bean("stockRedisTemplate")
    public RedisTemplate<String, Object> redisTemplate() {
        return super.redisTemplate();
    }

    @Bean("stockStringRedisTemplate")
    public StringRedisTemplate stringRedisTemplate() {
        return super.stringRedisTemplate();
    }

    @Bean("stockKeyGenerator")
    public KeyGenerator keyGenerator() {
        return super.keyGenerator();
    }
}
