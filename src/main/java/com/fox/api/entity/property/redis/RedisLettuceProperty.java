package com.fox.api.entity.property.redis;

import com.fox.api.entity.property.redis.lettuce.RedisLettucePoolProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * redis连接配置
 * @author lusongsong
 * @date 2020/3/25 17:23
 */
@Data
public class RedisLettuceProperty implements Serializable {
    /**
     * redis连接池配置
     */
    RedisLettucePoolProperty pool;
}
