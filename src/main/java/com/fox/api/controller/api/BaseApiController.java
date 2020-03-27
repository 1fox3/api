package com.fox.api.controller.api;

import com.fox.api.util.AESUtil;
import com.fox.api.dao.user.entity.UserLoginEntity;
import com.fox.api.entity.dto.login.LoginDto;
import com.fox.api.service.user.UserLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class BaseApiController {
    @Autowired
    protected UserLoginService userLoginService;

    @Value("${login.aes.key}")
    private String loginAesKey;

    /**
     * 用户id
     */
    protected Integer userId;
    /**
     * 用户登录信息
     */
    protected UserLoginEntity userLoginEntity;

    /**
     * 获取用户id
     * @return
     */
    public Integer getUserId() {
        if (null == this.userId) {
            this.getUserBySession();
        }
        return this.userId;
    }

    /**
     * 根据cookie里的sessionid获取用户id
     */
    public void getUserBySession() {
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null != servletRequestAttributes) {
            HttpServletRequest httpServletRequest = servletRequestAttributes.getRequest();
            if (null != httpServletRequest) {
                Cookie[] cookies = httpServletRequest.getCookies();
                if (null != cookies) {
                    for(Cookie cookie : cookies) {
                        if ("sessionid".equals(cookie.getName())) {
                            try {
                                Integer sessionid = Integer.valueOf(
                                        AESUtil.decrypt(String.valueOf(cookie.getValue()), this.loginAesKey)
                                );
                                if (null != sessionid) {
                                    this.userLoginEntity = userLoginService.getUserLoginBySessionid(sessionid);
                                    if (null != this.userLoginEntity) {
                                        this.userId = this.userLoginEntity.getUserId();
                                    }
                                }
                            } catch (Exception e) {}
                        }
                    }
                }
            }
        }
    }

    /**
     * 设置登录的cookie
     * @param loginDTO
     */
    public void setSessionCookie(LoginDto loginDTO) {
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null != servletRequestAttributes) {
            HttpServletResponse httpServletResponse = servletRequestAttributes.getResponse();
            Cookie cookie = new Cookie("sessionid", loginDTO.getSessionid());
            cookie.setDomain("1fox3.com");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(loginDTO.getExpireTime());
            httpServletResponse.addCookie(cookie);
        }
    }
}
