package com.fox.api.service.user;

import com.fox.api.dao.user.entity.StockHelperUserInfoEntity;
import com.fox.api.entity.dto.login.LoginDto;

/**
 * 股票助手用户服务
 * @author lusongsong
 */
public interface StockHelperUserService {
    /**
     * 发送验证码
     * @param account
     * @return
     */
    boolean sendCode(String account);

    /**
     * 获取用户信息
     * @param account
     * @return
     */
    StockHelperUserInfoEntity getInfoByAccount(String account);

    /**
     * 用户登录
     * @param account
     * @param verifyCode
     * @return
     */
    LoginDto login(String account, String verifyCode);

    /**
     * 退出登录
     * @param account
     * @param sessionid
     * @return
     */
    boolean logout(String account, String sessionid);
}
