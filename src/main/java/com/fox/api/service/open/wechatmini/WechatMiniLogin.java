package com.fox.api.service.open.wechatmini;

import com.fox.api.controller.vo.open.wechatmini.login.WechatMiniLoginVO;
import com.fox.api.service.open.dto.login.LoginDTO;

public interface WechatMiniLogin {
    public LoginDTO login(WechatMiniLoginVO wechatMiniLoginVO);
}
