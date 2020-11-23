package com.fox.api.configuration.cache;

import com.fox.api.entity.property.redis.RedisLettuceProperty;
import com.fox.api.property.redis.ClassCacheTimeProperty;
import lombok.Data;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * redis配置基类
 */
@Data
public abstract class AbstractRedisCacheConfig {
    @Autowired
    ClassCacheTimeProperty classCacheTimeProperty;

    protected Integer database;
    protected String host;
    protected Integer port;
    protected String password;
    protected String timeout;
    protected RedisLettuceProperty lettuce;

    public GenericObjectPoolConfig redisPool() {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxIdle(this.lettuce.getPool().getMaxIdle());
        genericObjectPoolConfig.setMinIdle(this.lettuce.getPool().getMinIdle());
        genericObjectPoolConfig.setMaxTotal(this.lettuce.getPool().getMaxActive());
        return genericObjectPoolConfig;
    }

    public RedisStandaloneConfiguration redisConfig() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setDatabase(this.database);
        redisStandaloneConfiguration.setHostName(this.host);
        redisStandaloneConfiguration.setPassword(this.password);
        redisStandaloneConfiguration.setPort(this.port);
        return redisStandaloneConfiguration;
    }

    public LettuceConnectionFactory factory() {
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder().poolConfig(this.redisPool()).build();
        return new LettuceConnectionFactory(this.redisConfig(), clientConfiguration);
    }

    public RedisTemplate<String, Object> redisTemplate() {
        return this.getRedisTemplate(this.factory());
    }

    public StringRedisTemplate stringRedisTemplate() {
        return this.getStringRedisTemplate(this.factory());
    }

    protected RedisTemplate<String, Object> getRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        RedisSerializer stringSerializer = new StringRedisSerializer();
        // key采用String的序列化方式
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setConnectionFactory(factory);
        template.afterPropertiesSet();
        return template;
    }

    protected StringRedisTemplate getStringRedisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(factory);
        template.afterPropertiesSet();
        return template;
    }

    public KeyGenerator keyGenerator() {
        return (o, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(o.getClass().getName()); // 类目
            sb.append(method.getName()); // 方法名
            for (Object param : params) {
                sb.append(param.toString()); // 参数名
            }
            return sb.toString();
        };
    }

    public RedisCacheManager cacheManager() {
        RedisConnectionFactory connectionFactory = this.factory();
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(3600)) // 1小时缓存失效
                // 不缓存null值
                .disableCachingNullValues();

        RedisCacheManager redisCacheManager = RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .transactionAware()
                .withInitialCacheConfigurations(this.getRedisCacheConfigurationMap())
                .build();
        return redisCacheManager;
    }

    private Map<String, RedisCacheConfiguration> getRedisCacheConfigurationMap() {
        Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : this.classCacheTimeProperty.getTime().entrySet()) {
            String mapKey = entry.getKey();
            Integer mapValue = entry.getValue();
            redisCacheConfigurationMap.put(mapKey, this.getRedisCacheConfigurationWithTtl(mapValue));
        }
        return redisCacheConfigurationMap;
    }

    private RedisCacheConfiguration getRedisCacheConfigurationWithTtl(Integer seconds) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        return redisCacheConfiguration;
    }
}
