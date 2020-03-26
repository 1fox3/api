package com.fox.api.util.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserRedisUtil extends RedisUtil {
    @Autowired
    @Qualifier("userRedisTemplate")
    protected RedisTemplate redisTemplate;


    @Override
    public RedisTemplate getRedisTemplate() {
        return this.redisTemplate;
    }
}
