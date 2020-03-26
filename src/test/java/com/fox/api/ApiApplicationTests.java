package com.fox.api;

import com.fox.api.property.redis.ClassCacheTimeProperty;
import com.fox.api.service.user.UserLoginService;
import com.fox.api.util.redis.UserRedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApiApplicationTests {
    @Autowired
    ClassCacheTimeProperty classCacheTimeProperty;
    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private UserRedisUtil userRedisUtil;

    @Test
    void contextLoads() {
    }

    @Test
    void redisTest() {
        String key = "UserLogin::1";
        System.out.println(userLoginService.getUserLoginBySessionid(1));
        System.out.println(String.valueOf(userRedisUtil.getExpire(key)));
    }
}
