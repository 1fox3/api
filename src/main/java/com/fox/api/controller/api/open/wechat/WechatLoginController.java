package com.fox.api.controller.api.open.wechat;

import com.fox.api.controller.api.BaseApiController;
import com.fox.api.controller.entity.result.Result;
import com.fox.api.service.open.entity.login.LoginEntity;
import com.fox.api.service.open.wechat.WechatLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@CrossOrigin(origins = "*")//处理跨域问题
@RestController
public class WechatLoginController extends BaseApiController {
    @Autowired
    private WechatLogin wechatLogin;

    @RequestMapping("/open/wechat/login")
    public Result login(Integer userId) {
        System.out.println(this.getUserId());
        LoginEntity loginEntity = wechatLogin.login(userId);
        this.setSessionCookie(loginEntity);
        return Result.success(loginEntity);
    }
    @RequestMapping("/open/wechat/showUser")
    public Result showUser() {
        return Result.success(this.getUserId());
    }
}
