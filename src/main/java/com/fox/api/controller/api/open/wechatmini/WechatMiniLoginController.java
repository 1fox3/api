package com.fox.api.controller.api.open.wechatmini;

import com.fox.api.controller.api.BaseApiController;
import com.fox.api.controller.entity.result.Result;
import com.fox.api.service.open.entity.login.LoginEntity;
import com.fox.api.service.open.wechatmini.WechatMiniLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@CrossOrigin(origins = "*")//处理跨域问题
@RestController
public class WechatMiniLoginController extends BaseApiController {

    @Autowired
    private WechatMiniLogin wechatMiniLogin;

    @RequestMapping("/open/wechatmini/login")
    public Result login(String code, String iv, String encryptedData, String platId) {
        LoginEntity loginEntity = wechatMiniLogin.login(code, iv, encryptedData, platId);
        this.setSessionCookie(loginEntity);
        return Result.success(loginEntity);
    }
}
