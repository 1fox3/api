package com.fox.api.controller.api.open.wechat;

import com.fox.api.controller.api.BaseApiController;
import com.fox.api.controller.dto.result.ResultDTO;
import com.fox.api.controller.vo.UserVo;
import com.fox.api.service.open.dto.login.LoginDTO;
import com.fox.api.service.open.wechat.WechatLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")//处理跨域问题
@RestController
public class WechatLoginController extends BaseApiController {
    @Autowired
    private WechatLogin wechatLogin;

    @RequestMapping("/open/wechat/login")
    public ResultDTO login(@RequestBody UserVo userVo) {
        System.out.println(this.getUserId());
        LoginDTO loginEntity = wechatLogin.login(userVo.getUserId());
        this.setSessionCookie(loginEntity);
        return ResultDTO.success(loginEntity);
    }
}
