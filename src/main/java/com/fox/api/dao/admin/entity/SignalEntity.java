package com.fox.api.dao.admin.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 信号
 * @author lusongsong
 * @date 2020/6/15 16:34
 */
@Data
public class SignalEntity {
    /**
     * 记录id
    */
    Integer id;
    /**
     * 信号
    */
    String signal;
    /**
     * 信号值
    */
    String signalValue;
    /**
     * 开始处理时间
    */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String startHandleTime;
    /**
     * 处理结束时间
    */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String endHandleTime;
    /**
     * 处理状态
    */
    String handleStatus;
    /**
     * 记录创建时间
    */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String createTime;
    /**
     * 记录更新时间
    */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String updateTime;
}
