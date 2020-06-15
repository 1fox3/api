package com.fox.api.annotation.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author lusongsong
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
@Mapper
public @interface AdminMapperConfig {
    String value() default "";
}
