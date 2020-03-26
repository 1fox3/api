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
@ConfigurationProperties("spring.redis.user")
public class UserRedisCacheConfig extends AbstractRedisCacheConfig {
    @Bean("userRedisFactory")
    public LettuceConnectionFactory factory() {
        return super.factory();
    }

    @Bean("userRedisTemplate")
    public RedisTemplate<String, Object> redisTemplate() {
        return super.redisTemplate();
    }

    @Bean("userStringRedisTemplate")
    public StringRedisTemplate stringRedisTemplate() {
        return super.stringRedisTemplate();
    }

    @Bean("userKeyGenerator")
    public KeyGenerator keyGenerator() {
        return super.keyGenerator();
    }
}
