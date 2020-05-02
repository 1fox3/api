package com.fox.api.service.quartz;

import com.fox.api.dao.quartz.entity.QuartzJobParamEntity;

import java.util.List;

/**
 * 计划任务启动类
 * @author lusongsong
 */
public interface QuartzService {
    /**
     * 根据类名启动任务
     * @param beanName
     */
    void executeTask(String beanName);

    /**
     * 根据类名和方法名启动任务
     * @param beanName
     * @param methodName
     */
    void executeTask(String beanName,String methodName);

    /**
     * 带参数的计划任务
     * @param beanName
     * @param methodName
     * @param paramList
     */
    void executeTask(String beanName, String methodName, List paramList);
}
