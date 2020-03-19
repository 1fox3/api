package com.fox.api.service.open.wechat;

import com.fox.api.service.open.entity.login.LoginEntity;

public interface WechatLogin {
    public LoginEntity login(Integer userId);
    public LoginEntity login(String userId, String platId);
}
