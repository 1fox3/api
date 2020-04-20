package com.fox.api.annotation.aspect.log;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
/**
 * @author lusongsong
 */
public @interface LogShowTimeAnt {
}
