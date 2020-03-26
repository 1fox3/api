package com.fox.api.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.Serializable;

public class RedisUtil {
    @Autowired
    private RedisTemplate redisTemplate; //操作k-v都是对象的

    @Autowired
    private RedisTemplate<Serializable, Object> jsonRedisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate; //操作k-v都是字符串的


}
