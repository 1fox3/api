package com.fox.api.property.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "redis.class")
@Data
public class ClassCacheTimeProperty {
    Map<String, Integer> time;
}
