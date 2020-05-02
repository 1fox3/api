package com.fox.api.service.quartz.impl;

import com.fox.api.dao.quartz.entity.QuartzJobParamEntity;
import com.fox.api.service.quartz.QuartzService;
import com.fox.api.util.ApplicationContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * 计划任务启动类
 * @author lusongsong
 */
@Service
public class QuartzServiceImpl implements QuartzService {
    private static final Logger logger = LoggerFactory.getLogger(QuartzServiceImpl.class);
    private static final String METHOD_NAME = "execute";

    @Override
    public void executeTask(String beanName) {
        executeTask(beanName, METHOD_NAME);
    }

    @Override
    public void executeTask(String beanName, String methodName) {
        executeTask(beanName, methodName, new LinkedList<>());
    }

    @Override
    public void executeTask(String beanName, String methodName, List paramList) {
        try {
            Integer paramSize = null == paramList ? 0 : paramList.size();
            Class[] argsClass = new Class[paramSize];
            Object[] args = new Object[paramSize];
            if (null != paramList || 0 < paramList.size()) {
                for (int i = 0; i < paramList.size(); i++) {
                    if(paramList.get(i) != null) {
                        argsClass[i] = paramList.get(i).getClass();
                        args[i] = paramList.get(i);
                    }
                }
            }
//            logger.info("{}反射调{}.{}法开始", this.getClass().getSimpleName(), beanName, methodName);
            if (beanName.contains("\\.")){
                Class clazz = Class.forName(beanName);
                Object cronJob =  ApplicationContextUtil.getBean(clazz);
                if (0 == paramSize) {
                    Method method1 = clazz.getMethod(methodName);
                    method1.invoke(cronJob);
                } else {
                    Method method1 = clazz.getMethod(methodName, argsClass);
                    method1.invoke(cronJob, args);
                }

            } else {
                Object object = ApplicationContextUtil.getBean(beanName);
                if (0 == paramSize) {
                    Method method = object.getClass().getMethod(methodName);
                    method.invoke(object);
                } else {
                    Method method = object.getClass().getMethod(methodName, argsClass);
                    method.invoke(object, args);
                }
            }
        } catch (Exception e) {
//            logger.error("{} method invoke error,{}.{}", this.getClass().getSimpleName(), beanName, methodName);
            return;
        }
//        logger.info("{}反射调{}.{}法结束", this.getClass().getSimpleName(), beanName, methodName);
    }
}
