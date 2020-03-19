package com.fox.api.service.open.wechatmini;

import com.fox.api.service.open.entity.login.LoginEntity;

public interface WechatMiniLogin {
    public LoginEntity login(String code, String iv, String encryptedData, String platId);
}
