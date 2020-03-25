package com.fox.api.service.open.dto.login;

import com.fox.api.util.AESUtil;
import lombok.Data;

@Data
public class LoginDTO {
    //登录的sessionid
    private String sessionid;

    //过期时间
    private Integer expireTime;

    /**
     * 设置sessionid
     * @param sessionid
     * @param aesKey
     */
    public void setSessionid(Integer sessionid, String aesKey)
    {
        if (null != sessionid && null != aesKey) {
            String session = String.valueOf(sessionid);
            try {
                this.sessionid = AESUtil.encrypt(session, aesKey);
            } catch (Exception e) {}
        }
    }

    /**
     * 获取登录信息
     * @param aesKey
     * @return
     */
    public String getSessionid(String aesKey)
    {
        if (null != aesKey) {
            try {
                String session = AESUtil.decrypt(this.sessionid, aesKey);
                return session;
            } catch (Exception e) {}
        }
        return "";
    }

    /**
     * 获取过期时间
     * @return
     */
    public Integer getExpireTime() {
        return expireTime;
    }

    /**
     * 设置过期时间
     * @param expireTime
     */
    public void setExpireTime(Integer expireTime) {
        this.expireTime = expireTime;
    }
}
