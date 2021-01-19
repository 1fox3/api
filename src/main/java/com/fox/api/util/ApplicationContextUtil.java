package com.fox.api.util;

import org.springframework.lang.Nullable;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 上下文工具类
 *
 * @author lusongsong
 * @date 2021/1/13 18:09
 */
@Component
public class ApplicationContextUtil implements ApplicationContextAware {
    /**
     * 上下文
     */
    private static ApplicationContext applicationContext;

    /**
     * 设置上下文
     *
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextUtil.applicationContext = applicationContext;
    }

    /**
     * 根据类名获取实现类
     *
     * @param var1
     * @param <T>
     * @return
     */
    public static <T> Map<String, T> getBeansOfType(@Nullable Class<T> var1) {
        return ApplicationContextUtil.applicationContext.getBeansOfType(var1);
    }

    /**
     * 根据bean的名称去寻找一个类的实例
     *
     * @param name
     * @param <T>
     * @return
     */
    public static <T> T getBean(String name) {

        return (T) applicationContext.getBean(name);
    }

    /**
     * 根据全限定类名去寻找一个spring管理的bean实例
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> clazz) {

        return (T) applicationContext.getBean(clazz);
    }

    /**
     * 根据bean的名称和全限定类名去寻找一个类的实例
     *
     * @param name
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return (T) applicationContext.getBean(name, clazz);
    }
}
