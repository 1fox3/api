package com.fox.api.service.open.wechatmini;

import com.fox.api.entity.vo.open.wechatmini.login.WechatMiniLoginVo;
import com.fox.api.service.open.dto.login.LoginDTO;

public interface WechatMiniLogin {
    public LoginDTO login(WechatMiniLoginVo wechatMiniLoginVO);
}
