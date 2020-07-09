package com.fox.api.service.open.wechat.impl;

import com.fox.api.dao.user.entity.UserEntity;
import com.fox.api.dao.user.entity.UserLoginEntity;
import com.fox.api.dao.user.mapper.UserMapper;
import com.fox.api.entity.dto.login.LoginDto;
import com.fox.api.service.open.wechat.WechatLogin;
import com.fox.api.service.user.UserLoginService;
import com.fox.api.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class WechatLoginImpl implements WechatLogin {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserLoginService userLoginService;

    /**
     * 默认登录时间
     */
    private static Integer loginExpireDate = 30;

    @Override
    public LoginDto login(Integer userId) {
        LoginDto loginEntity = new LoginDto();
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
        String sessionid = userLoginService.login(userLoginEntity);
        loginEntity.setSessionid(sessionid);
        loginEntity.setExpireTime(WechatLoginImpl.loginExpireDate*86400);
        return loginEntity;
    }

    @Override
    public LoginDto login(String userId, String platId) {
        UserEntity userEntity = this.userMapper.getByPlatUserId(userId, platId, 1);
        if (null != userEntity && null != userEntity.getId()) {
            return this.login(userEntity.getId());
        }
        return new LoginDto();
    }
}
