package com.fox.api.dao.user.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 股票助手用户
 * @author lusongsong 
 */
@Data
public class StockHelperUserInfoEntity {
    /**
     * 记录id
     */
    private Integer id;
    /**
     * 账号
    */
    private String account;
    /**
     * 账号类型
     */
    private Integer type = 1;
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
