package com.fox.api.service.quartz;

/**
 * 计划任务启动类
 * @author lusongsong
 */
public interface QuartzService {

    /**
     * 根据类名和方法名启动任务
     * @param beanName
     * @param methodName
     */
    void executeTask(String beanName,String methodName);

    /**
     * 根据类名启动任务
     * @param beanName
     */
    void executeTask(String beanName);
}
