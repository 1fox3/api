package com.fox.api.service.open.wechat.impl;

import com.fox.api.util.DateUtil;
import com.fox.api.dao.user.entity.UserEntity;
import com.fox.api.dao.user.entity.UserLoginEntity;
import com.fox.api.dao.user.mapper.UserLoginMapper;
import com.fox.api.dao.user.mapper.UserMapper;
import com.fox.api.service.open.dto.login.LoginDTO;
import com.fox.api.service.open.wechat.WechatLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class WechatLoginImpl implements WechatLogin {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserLoginMapper userLoginMapper;

    @Value("${login.aes.key}")
    private String loginAesKey;

    /**
     * 默认登录时间
     */
    private static Integer loginExpireDate = 30;

    @Override
    public LoginDTO login(Integer userId) {
        LoginDTO loginEntity = new LoginDTO();
        UserEntity userEntity = this.userMapper.getById(userId);
        if (null == userEntity.getId()) {
            return loginEntity;
        }
        UserLoginEntity userLoginEntity = new UserLoginEntity();
        userLoginEntity.setUserId(userId);
        Date loginDate = new Date();
        String loginTime = DateUtil.dateToStr(loginDate, DateUtil.TIME_FORMAT_1);
        String expireTime = DateUtil.getRelateDate(
                loginTime, 0,0, WechatLoginImpl.loginExpireDate, DateUtil.TIME_FORMAT_1
        );
        userLoginEntity.setLoginTime(loginTime);
        userLoginEntity.setExpireTime(expireTime);
        userLoginMapper.insert(userLoginEntity);
        Integer sessionid = userLoginEntity.getId();
        loginEntity.setSessionid(sessionid, this.loginAesKey);
        loginEntity.setExpireTime(WechatLoginImpl.loginExpireDate*86400);
        return loginEntity;
    }

    @Override
    public LoginDTO login(String userId, String platId) {
        UserEntity userEntity = this.userMapper.getByPlatUserId(userId, platId, 1);
        if (null != userEntity && null != userEntity.getId()) {
            return this.login(userEntity.getId());
        }
        return new LoginDTO();
    }
}
