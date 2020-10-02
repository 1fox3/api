package com.fox.api.dao.stock.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 股票关注列表
 * @author lusongsong
 * @date 2020/3/5 18:13
 */
@Data
public class StockFollowEntity {
    /**
     * 记录id
     */
    Integer id;
    /**
     * 用户id
     */
    Integer userId;
    /**
     * 股票id
     */
    Integer stockId;
    /**
     * 关注状态
     */
    Integer followStatus;
    /**
     * 关注时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String followTime;
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
