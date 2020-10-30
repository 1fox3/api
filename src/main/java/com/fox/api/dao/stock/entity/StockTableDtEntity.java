package com.fox.api.dao.stock.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;


/**
 * 数据表数据日期管理
 *
 * @author lusongsong 
 * @date 2020/10/30 16:52
 */
@Data
public class StockTableDtEntity {
    /**
     * 记录id
    */
    Integer id;
    /**
     * 表
    */
    Integer table;
    /**
     * 日期
    */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    String dt;
    /**
     * 是否已备份
    */
    Integer type;
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
