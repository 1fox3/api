package com.fox.api.annotation.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 计划任务管理数据源注解
 * @author lusongsong
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
@Mapper
public @interface QuartzMapperConfig {
    String value() default "";
}
