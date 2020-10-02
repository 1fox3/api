package com.fox.api.entity.dto.login;

import lombok.Data;

/**
 * 登录返回结果
 * @author lusongsong
 * @date 2020/3/18 16:58
 */
@Data
public class LoginDto {
    /**
     * 登录的sessionid
     */
    String sessionid;

    /**
     * 过期时间
     */
    Integer expireTime;
}
