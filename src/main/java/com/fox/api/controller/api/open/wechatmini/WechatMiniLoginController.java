package com.fox.api.controller.api.open.wechatmini;

import com.fox.api.controller.api.BaseApiController;
import com.fox.api.controller.dto.result.ResultDTO;
import com.fox.api.controller.enums.code.LoginCode;
import com.fox.api.controller.enums.code.ReturnCode;
import com.fox.api.controller.vo.open.wechatmini.login.WechatMiniLoginVO;
import com.fox.api.service.open.dto.login.LoginDTO;
import com.fox.api.service.open.wechatmini.WechatMiniLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@CrossOrigin(origins = "*")//处理跨域问题
@RestController
@RequestMapping("/open/wechatmini")
public class WechatMiniLoginController extends BaseApiController {

    @Autowired
    private WechatMiniLogin wechatMiniLogin;

    @RequestMapping("/login")
    public ResultDTO login(@Valid @RequestBody WechatMiniLoginVO wechatMiniLoginVO) {
        LoginDTO loginDTO = wechatMiniLogin.login(wechatMiniLoginVO);
        if (null == loginDTO.getSessionid()) {
            return ResultDTO.fail(LoginCode.LOGIN_FAIL);
        }
        return ResultDTO.success(loginDTO);
    }
}
