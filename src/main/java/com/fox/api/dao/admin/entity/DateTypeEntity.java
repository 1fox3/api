package com.fox.api.dao.admin.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;


/**
 * 日期类型
 * @author lusongsong 
 * @date 2020/10/05 16:49
 */
@Data
public class DateTypeEntity {
    /**
     * 记录id
    */
    Integer id;
    /**
     * 日期
    */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    String dt;
    /**
     * 日期类型,0(未知),1(工作日),2(周末),3(假期),4(调班)
    */
    Integer type;
    /**
     * 创建时间
    */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String createTime;
}
