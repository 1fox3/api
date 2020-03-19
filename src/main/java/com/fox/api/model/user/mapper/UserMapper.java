package com.fox.api.model.user.mapper;

import com.fox.api.common.config.mapper.UserMapperConfig;
import com.fox.api.model.user.entity.UserEntity;

@UserMapperConfig
public interface UserMapper {
    Integer insert(UserEntity userEntity);
    Integer update(UserEntity userEntity);
    UserEntity getByPlatUserId(String platUserId, String platId, Integer platType);
    UserEntity getById(Integer id);
}
