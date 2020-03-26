package com.fox.api.entity.property.redis.lettuce;

import lombok.Data;

import java.io.Serializable;

@Data
public class RedisLettucePoolProperty implements Serializable {
    private Integer maxActive;
    private Integer minIdle;
    private Integer maxIdle;
    private String maxWait;
}
