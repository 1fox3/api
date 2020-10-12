package com.fox.api.dao.quartz.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author lusongsong 
 * @date 2020/10/12 15:51
 */
@Data
public class JobRunLogEntity {
    /**
     * 记录id
    */
    Integer id;
    /**
     * bean名称
    */
    String beanName;
    /**
     * 方法名
    */
    String methodName;
    /**
     * 时间
    */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String logTime;
    /**
     * 信息
    */
    String info;
}
