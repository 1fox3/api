package com.fox.api.dao.quartz.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 计划任务参数
 * @author lusongsong
 */
@Data
public class QuartzJobParamEntity {
    /**
     * 记录id
     */
    private Integer id;
    /**
     * 任务id
     */
    private Integer jobId;
    /**
     * 任务参数序号
     */
    private String paramIdx;
    /**
     * 任务参数类型
     */
    private String paramType;
    /**
     * 任务参数值
     */
    private String paramValue;
    /**
     * 任务参数描述
     */
    private String note;
    /**
     * 是否已删除
     */
    private Integer isDeleted;
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
