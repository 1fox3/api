package com.fox.api.util.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

public abstract class RedisUtil {
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 获取缓存模板
     * @return
     */
    public abstract RedisTemplate getRedisTemplate();

    /**
     * 指定缓存失败时间
     * @param key
     * @param time
     * @return
     */
    public boolean expire(Object key, Long time) {
        try {
            if (time > 0) {
                this.getRedisTemplate().expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Throwable e) {
            logger.error("redis操作失败", e);
            return false;
        }
    }

    /**
     * 获取过期时间
     * @param key
     * @return
     */
    public Long getExpire(Object key) {
        return this.getRedisTemplate().getExpire(key, TimeUnit.SECONDS);
    }
}
