package com.fox.api.dao.stock.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fox.api.util.DateUtil;
import lombok.Data;


/**
 * 分钟日期管理
 * @author lusongsong 
 * @date 2020/10/16 16:23
 */
@Data
public class StockPriceMinuteDtEntity {
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
     * 是否已备份
    */
    Integer type = 0;
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
