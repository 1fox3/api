package com.fox.api.service.user;

import com.fox.api.dao.user.entity.UserLoginEntity;

/**
 * 用户登录
 * @author lusongsong
 */
public interface UserLoginService {
    /**
     * 根据sessionid获取登录信息
     * @param sessionid
     * @return
     */
    UserLoginEntity getUserLoginBySessionid(Integer sessionid);
}
