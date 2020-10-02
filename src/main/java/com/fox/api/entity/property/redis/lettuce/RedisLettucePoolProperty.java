package com.fox.api.entity.property.redis.lettuce;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lusongsong
 * @date 2020/3/25 17:10
 */
@Data
public class RedisLettucePoolProperty implements Serializable {
    /**
     * 最大连接数
     */
    Integer maxActive;
    /**
     * 最小等待连接中的数量
     */
    Integer minIdle;
    /**
     * 最大等待连接中的数量
     */
    Integer maxIdle;
    /**
     * 最大等待毫秒数
     */
    String maxWait;
}
