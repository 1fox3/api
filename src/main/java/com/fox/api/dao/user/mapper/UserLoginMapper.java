package com.fox.api.dao.user.mapper;

import com.fox.api.annotation.mapper.UserMapperConfig;
import com.fox.api.dao.user.entity.UserLoginEntity;

@UserMapperConfig
public interface UserLoginMapper {
    Integer insert(UserLoginEntity userLoginEntity);
    Integer update(UserLoginEntity userLoginEntity);
    UserLoginEntity getById(Integer id);
}
