package com.fox.api.service.user;

import com.fox.api.dao.user.entity.StockHelperUserInfoEntity;

import java.io.UnsupportedEncodingException;

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
    boolean sendCode(String account) throws UnsupportedEncodingException;

    /**
     * 验证验证码
     * @param account
     * @param verifyCode
     * @return
     */
    boolean verifyCode(String account, String verifyCode);

    /**
     * 获取用户信息
     * @param account
     * @return
     */
    StockHelperUserInfoEntity getInfoByAccount(String account);

    /**
     * 验证账号密码
     * @param account
     * @param pwd
     * @return
     */
    boolean verifyPwd(String account, String pwd);

    /**
     * 注册
     * @param account
     * @param pwd
     * @param verifyCode
     * @return
     */
    boolean register(String account, String pwd, String verifyCode);

    /**
     * 用户登录
     * @param account
     * @param pwd
     * @param verifyCode
     * @return
     */
    boolean login(String account, String pwd, String verifyCode);

    /**
     * 无验证码登录
     * @param account
     * @param pwd
     * @return
     */
    boolean login(String account, String pwd);

    /**
     * 退出登录
     * @param account
     * @param pwd
     * @param sessionid
     * @return
     */
    boolean logout(String account, String pwd, String sessionid);
}
