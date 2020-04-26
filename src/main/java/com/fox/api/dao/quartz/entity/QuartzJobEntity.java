package com.fox.api.dao.quartz.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Id;

/**
 * 计划任务管理对象
 * @author lusongsong
 */
@Data
public class QuartzJobEntity {
    /**
     * 记录id
     */
    private Integer id;
    /**
     * 任务id
     */
    private String jobKey;
    /**
     * 任务名
     */
    private String jobName;
    /**
     * 任务状态
     */
    private String jobStatus;
    /**
     * 任务分组
     */
    private String jobGroup;
    /**
     * 任务执行时间
     */
    private String cronExpr;
    /**
     * 任务描述
     */
    private String note;
    /**
     * bean名称
     */
    private String beanName;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String updateTime;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String createTime;
}
