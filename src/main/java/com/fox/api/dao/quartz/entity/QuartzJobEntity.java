package com.fox.api.dao.quartz.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 计划任务管理对象
 * @author lusongsong
 * @date 2020/4/24 15:48
 */
@Data
public class QuartzJobEntity {
    /**
     * 记录id
     */
    Integer id;
    /**
     * 任务id
     */
    String jobKey;
    /**
     * 任务名
     */
    String jobName;
    /**
     * 任务状态
     */
    String jobStatus;
    /**
     * 任务分组
     */
    String jobGroup;
    /**
     * 任务执行时间
     */
    String cronExpr;
    /**
     * 任务描述
     */
    String note;
    /**
     * bean名称
     */
    String beanName;
    /**
     * 方法名
     */
    String methodName;
    /**
     * 是否使用锁
     */
    Integer useLock;
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String updateTime;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String createTime;
}
