package com.fox.api.service.open.wechat;

import com.fox.api.service.open.dto.login.LoginDTO;

public interface WechatLogin {
    public LoginDTO login(Integer userId);
    public LoginDTO login(String userId, String platId);
}
