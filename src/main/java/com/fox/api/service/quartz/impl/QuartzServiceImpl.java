package com.fox.api.service.quartz.impl;

import com.fox.api.service.quartz.QuartzService;
import com.fox.api.util.ApplicationContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;

/**
 * 计划任务启动类
 * @author lusongsong
 */
@Service
public class QuartzServiceImpl implements QuartzService {
    private static final Logger logger = LoggerFactory.getLogger(QuartzServiceImpl.class);
    private static final String METHOD_NAME = "execute";

    @Override
    public void executeTask(String beanName, String methodName) {
        Object object = ApplicationContextUtil.getBean(beanName);
        try {
            logger.info("{}反射调{}.{}法开始", this.getClass().getSimpleName(), beanName, methodName);
            if (beanName.contains("\\.")){
                Class clazz = Class.forName(beanName);
                Object cronJob =  ApplicationContextUtil.getBean(clazz);
                Method method1 = clazz.getMethod(methodName);
                method1.invoke(cronJob);
            } else {
                Method method = object.getClass().getMethod(methodName);
                method.invoke(object);
            }

        } catch (Exception e) {
            logger.error("{} method invoke error,{}.{}", this.getClass().getSimpleName(), beanName, methodName);
            return;
        }
        logger.info("{}反射调{}.{}法结束", this.getClass().getSimpleName(), beanName, methodName);

    }

    @Override
    public void executeTask(String beanName) {
        executeTask(beanName, METHOD_NAME);
    }
}
