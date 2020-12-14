package com.fox.api.service.user.impl;

import com.fox.api.dao.user.entity.UserLoginEntity;
import com.fox.api.dao.user.mapper.UserLoginMapper;
import com.fox.api.exception.self.ServiceException;
import com.fox.api.service.user.UserLoginService;
import com.fox.api.util.AESUtil;
import com.fox.api.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 用户登录
 * @author lusongsong
 */
@Service
@CacheConfig(cacheNames = {"UserLogin"})
public class UserLoginImpl implements UserLoginService {
    @Autowired
    protected UserLoginMapper userLoginMapper;

    @Value("${login.aes.key}")
    private String loginAesKey;

    @Override
    @Cacheable(key = "#sessionid", cacheManager = "userCacheManager")
    public UserLoginEntity getUserLoginBySessionid(String sessionid) {
        try {
            Integer loginId = Integer.valueOf(AESUtil.decrypt(sessionid, this.loginAesKey));
            UserLoginEntity userLoginEntity = userLoginMapper.getById(loginId);
            //不返回登录id
            userLoginEntity.setId(null);
            if (null == userLoginEntity.getExpireTime()
                    || DateUtil.compare(userLoginEntity.getExpireTime(), DateUtil.getCurrentTime(), DateUtil.TIME_FORMAT_1) <= 0) {
                return null;
            }
            return userLoginEntity;
        } catch (Exception e) {
            throw new ServiceException(1, "获取用户登录信息失败");
        }
    }

    /**
     * 登录
     *
     * @param userLoginEntity
     * @return
     */
    @Override
    public String login(UserLoginEntity userLoginEntity) {
        if (null != userLoginEntity.getUserId()) {
            try {
                userLoginMapper.insert(userLoginEntity);
                return AESUtil.encrypt(userLoginEntity.getId().toString(), this.loginAesKey);
            } catch (Exception e) {
                throw new ServiceException(1, "用户登录失败");
            }
        }
        return "";
    }

    /**
     * 退出登录
     *
     * @param sessionid
     * @param userId
     * @return
     */
    @Override
    @CacheEvict(key = "#sessionid", cacheManager = "userCacheManager")
    public boolean logout(String sessionid, Integer userId) {
        try {
            UserLoginEntity userLoginEntity = new UserLoginEntity();
            userLoginEntity.setId(Integer.valueOf(AESUtil.decrypt(sessionid, this.loginAesKey)));
            userLoginEntity.setUserId(userId);
            userLoginEntity.setExpireTime(DateUtil.getCurrentTime());
            userLoginMapper.logout(userLoginEntity);
            return true;
        } catch (Exception e) {
            throw new ServiceException(1, "用户登录失败");
        }
    }
}
