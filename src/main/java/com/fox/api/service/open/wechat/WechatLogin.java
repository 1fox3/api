package com.fox.api.service.open.wechat;

import com.fox.api.entity.dto.login.LoginDto;

public interface WechatLogin {
    public LoginDto login(Integer userId);
    public LoginDto login(String userId, String platId);
}
