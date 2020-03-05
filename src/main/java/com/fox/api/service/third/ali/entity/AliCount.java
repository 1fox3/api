package com.fox.api.service.third.ali.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ali.api.account")
@Data
public class AliCount {
    private String appKey;
    private String appSecret;
    private String appCode;
}
