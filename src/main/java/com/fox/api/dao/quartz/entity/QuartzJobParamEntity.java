package com.fox.api.dao.quartz.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 计划任务参数
 * @author lusongsong
 * @date 2020/4/30 17:16
 */
@Data
public class QuartzJobParamEntity {
    /**
     * 记录id
     */
    Integer id;
    /**
     * 任务id
     */
    Integer jobId;
    /**
     * 任务参数序号
     */
    String paramIdx;
    /**
     * 任务参数类型
     */
    String paramType;
    /**
     * 任务参数值
     */
    String paramValue;
    /**
     * 任务参数描述
     */
    String note;
    /**
     * 是否已删除
     */
    Integer isDeleted;
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
