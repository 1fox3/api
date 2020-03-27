package com.fox.api.service.open.wechatmini;

import com.fox.api.entity.vo.open.wechatmini.login.WechatMiniLoginVo;
import com.fox.api.entity.dto.login.LoginDto;

public interface WechatMiniLogin {
    public LoginDto login(WechatMiniLoginVo wechatMiniLoginVO);
}
