package com.fox.api.service.user.impl;

import com.fox.api.dao.user.entity.UserLoginEntity;
import com.fox.api.dao.user.mapper.UserLoginMapper;
import com.fox.api.service.user.UserLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = {"UserLogin"})
public class UserLoginImpl implements UserLoginService {
    @Autowired
    protected UserLoginMapper userLoginMapper;

    @Override
    @Cacheable(key = "#sessionid", cacheManager = "userCacheManager")
    public UserLoginEntity getUserLoginBySessionid(Integer sessionid) {
        return userLoginMapper.getById(sessionid);
    }
}
