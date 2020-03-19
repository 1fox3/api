package com.fox.api.model.user.mapper;

import com.fox.api.common.config.mapper.UserMapperConfig;
import com.fox.api.model.user.entity.UserLoginEntity;

@UserMapperConfig
public interface UserLoginMapper {
    Integer insert(UserLoginEntity userLoginEntity);
    Integer update(UserLoginEntity userLoginEntity);
    UserLoginEntity getById(Integer id);
}
