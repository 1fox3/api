package com.fox.api.model.user.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class UserEntity {
    //记录id
    private Integer id;
    //平台类型
    private Integer platType = 0;
    //平台id
    private String platId = "";
    //平台用户id
    private String platUserId = "";
    //更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String updateTime;
    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String createTime;
}
