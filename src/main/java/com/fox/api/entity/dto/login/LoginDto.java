package com.fox.api.entity.dto.login;

import lombok.Data;

/**
 * 登录返回结果
 * @author lusongsong
 */
@Data
public class LoginDto {
    /**
     * 登录的sessionid
     */
    private String sessionid;

    /**
     * 过期时间
     */
    private Integer expireTime;
}
