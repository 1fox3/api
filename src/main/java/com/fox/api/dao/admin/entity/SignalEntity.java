package com.fox.api.dao.admin.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 信号
 * @author lusongsong 
 */
@Data
public class SignalEntity {
    /**
     * 记录id
    */
    private Integer id;
    /**
     * 信号
    */
    private String signal;
    /**
     * 信号值
    */
    private String signalValue;
    /**
     * 开始处理时间
    */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String startHandleTime;
    /**
     * 处理结束时间
    */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String endHandleTime;
    /**
     * 处理状态
    */
    private String handleStatus;
    /**
     * 记录创建时间
    */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String createTime;
    /**
     * 记录更新时间
    */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String updateTime;
}
