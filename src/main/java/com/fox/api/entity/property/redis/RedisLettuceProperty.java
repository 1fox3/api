package com.fox.api.entity.property.redis;

import com.fox.api.entity.property.redis.lettuce.RedisLettucePoolProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class RedisLettuceProperty implements Serializable {
    private RedisLettucePoolProperty pool;
}
