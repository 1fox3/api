package com.fox.api.util.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 股票redis操作工具类
 * @author lusongsong
 */
@Component
public class StockRedisUtil extends RedisUtil {
    @Autowired
    @Qualifier("stockRedisTemplate")
    protected RedisTemplate redisTemplate;

    @Override
    public RedisTemplate getRedisTemplate() {
        return this.redisTemplate;
    }
}
