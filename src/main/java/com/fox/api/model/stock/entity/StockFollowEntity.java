package com.fox.api.model.stock.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class StockFollowEntity {
    //记录id
    private Integer id;
    //用户id
    private Integer userId;
    //股票id
    private Integer stockId;
    //关注状态
    private Integer followStatus;
    //关注事件
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private String followTime;
    //更新时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private String updateTime;
    //创建时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private String createTime;
}
