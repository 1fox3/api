package com.fox.api;

import com.fox.api.service.user.UserLoginService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApiApplicationTests {
    @Autowired
    private UserLoginService userLoginService;

    @Test
    void contextLoads() {
    }

    @Test
    void redisTest() {
        System.out.println(userLoginService.getUserLoginBySessionid(1));
    }
}
