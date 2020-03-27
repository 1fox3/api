package com.fox.api.controller.api.open.wechat;

import com.fox.api.controller.api.BaseApiController;
import com.fox.api.entity.dto.result.ResultDto;
import com.fox.api.enums.code.LoginCode;
import com.fox.api.entity.dto.login.LoginDto;
import com.fox.api.service.open.wechat.WechatLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")//处理跨域问题
@RestController
public class WechatLoginController extends BaseApiController {
    @Autowired
    private WechatLogin wechatLogin;

    @RequestMapping("/open/wechat/login")
    public ResultDto login(Integer userId) {
        LoginDto loginDTO = wechatLogin.login(userId);
        this.setSessionCookie(loginDTO);
        if (null == loginDTO.getSessionid()) {
            return ResultDto.fail(LoginCode.LOGIN_FAIL);
        }
        return ResultDto.success(loginDTO);
    }
}
