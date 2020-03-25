package com.fox.api.controller.api.open.wechatmini;

import com.fox.api.controller.api.BaseApiController;
import com.fox.api.entity.dto.result.ResultDto;
import com.fox.api.enums.code.LoginCode;
import com.fox.api.entity.vo.open.wechatmini.login.WechatMiniLoginVo;
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
    public ResultDto login(@Valid @RequestBody WechatMiniLoginVo wechatMiniLoginVO) {
        LoginDTO loginDTO = wechatMiniLogin.login(wechatMiniLoginVO);
        if (null == loginDTO.getSessionid()) {
            return ResultDto.fail(LoginCode.LOGIN_FAIL);
        }
        return ResultDto.success(loginDTO);
    }
}
