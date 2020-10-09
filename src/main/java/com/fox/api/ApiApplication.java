package com.fox.api;

import com.fox.api.service.quartz.QuartzJobManageService;
import com.fox.api.util.ApplicationContextUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动类
 * @author lusongsong
 */
@SpringBootApplication
//@EnableScheduling
@EnableCaching
@ComponentScan(basePackages = {"com.fox.api.*"})
public class ApiApplication {

    /**
     * 加载所有计划任务
     */
    private static void loadTotalQuartzJob() {
        ((QuartzJobManageService)ApplicationContextUtil.getBean("quartzJobManageImpl")).loadTotalQuartzJob();
    }

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);

        //加载所有计划任务
//        ApiApplication.loadTotalQuartzJob();
    }

}
