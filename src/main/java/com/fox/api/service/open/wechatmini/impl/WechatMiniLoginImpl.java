package com.fox.api.service.open.wechatmini.impl;

import com.fox.api.entity.dto.http.HttpResponseDto;
import com.fox.api.util.DateUtil;
import com.fox.api.util.HttpUtil;
import com.fox.api.entity.vo.open.wechatmini.login.WechatMiniLoginVo;
import com.fox.api.dao.user.entity.UserEntity;
import com.fox.api.dao.user.entity.UserLoginEntity;
import com.fox.api.dao.user.mapper.UserLoginMapper;
import com.fox.api.dao.user.mapper.UserMapper;
import com.fox.api.entity.dto.login.LoginDto;
import com.fox.api.service.open.wechatmini.WechatMiniLogin;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class WechatMiniLoginImpl implements WechatMiniLogin {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserLoginMapper userLoginMapper;

    @Value("${open.wechat-mini.url.login}")
    private String wechatMiniLoginUrl;

    @Value("${login.aes.key}")
    private String loginAesKey;

    /**
     * 默认登录时间
     */
    private static Integer loginExpireDate = 30;

    @Override
    public LoginDto login(WechatMiniLoginVo wechatMiniLoginVO) {
        LoginDto loginEntity = new LoginDto();
        try {
            HttpUtil httpUtil = new HttpUtil();
            httpUtil.setUrl(this.wechatMiniLoginUrl);
            httpUtil.setParam("code", wechatMiniLoginVO.getCode());
            httpUtil.setParam("iv", wechatMiniLoginVO.getIv());
            httpUtil.setParam("encryptedData", wechatMiniLoginVO.getEncryptedData());
            httpUtil.setParam("wechatMini", wechatMiniLoginVO.getPlatId());
            HttpResponseDto httpResponse = httpUtil.request();
            String response = httpResponse.getContent();
            JSONObject jsonObject = JSONObject.fromObject(response);
            JSONObject data = jsonObject.getJSONObject("data");
            String platUserId = data.getString("openid");
            UserEntity userEntity = this.userMapper.getByPlatUserId(platUserId, wechatMiniLoginVO.getPlatId(), 2);
            if (null == userEntity.getId()) {
                return loginEntity;
            }
            UserLoginEntity userLoginEntity = new UserLoginEntity();
            userLoginEntity.setUserId(userEntity.getId());
            Date loginDate = new Date();
            String loginTime = DateUtil.dateToStr(loginDate, DateUtil.TIME_FORMAT_1);
            String expireTime = DateUtil.getRelateDate(
                    loginTime, 0,0, WechatMiniLoginImpl.loginExpireDate, DateUtil.TIME_FORMAT_1
            );
            userLoginEntity.setLoginTime(loginTime);
            userLoginEntity.setExpireTime(expireTime);
            userLoginMapper.insert(userLoginEntity);
            Integer sessionid = userLoginEntity.getId();
            loginEntity.setSessionid(sessionid, this.loginAesKey);
            loginEntity.setExpireTime(WechatMiniLoginImpl.loginExpireDate*86400);
        } catch (Exception e) {}
        return loginEntity;
    }
}
