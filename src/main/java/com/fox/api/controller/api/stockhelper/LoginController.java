package com.fox.api.controller.api.stockhelper;

import com.fox.api.controller.api.BaseApiController;
import com.fox.api.entity.dto.result.ResultDto;
import com.fox.api.service.user.StockHelperUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录
 * @author lusongsong
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/stockhelper/login/")
public class LoginController extends BaseApiController {

    @Autowired
    StockHelperUserService stockHelperUserService;

    @RequestMapping("login")
    public ResultDto login(String account, String verifyCode) {
        return ResultDto.success(stockHelperUserService.login(account, verifyCode));
    }

    @RequestMapping("logout")
    public ResultDto logout(String account, String sessionid) {
        return ResultDto.success(stockHelperUserService.logout(account, sessionid));
    }

    @RequestMapping("sendCode")
    public ResultDto sendCode(String account) {
        return ResultDto.success(stockHelperUserService.sendCode(account));
    }
}
