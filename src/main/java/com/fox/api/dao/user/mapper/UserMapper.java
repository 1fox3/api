package com.fox.api.dao.user.mapper;

import com.fox.api.annotation.mapper.UserMapperConfig;
import com.fox.api.dao.user.entity.UserEntity;

@UserMapperConfig
public interface UserMapper {
    Integer insert(UserEntity userEntity);
    Integer update(UserEntity userEntity);
    UserEntity getByPlatUserId(String platUserId, String platId, Integer platType);
    UserEntity getById(Integer id);
}
