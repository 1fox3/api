package com.fox.api.dao.user.mapper;

import com.fox.api.annotation.mapper.UserMapperConfig;
import com.fox.api.dao.user.entity.UserEntity;

/**
 * 用户表
 * @author lusongsong
 */
@UserMapperConfig
public interface UserMapper {
    /**
     * 添加记录
     * @param userEntity
     * @return
     */
    Integer insert(UserEntity userEntity);

    /**
     * 更新记录
     * @param userEntity
     * @return
     */
    Integer update(UserEntity userEntity);

    /**
     * 查询用户
     * @param platUserId
     * @param platId
     * @param platType
     * @return
     */
    UserEntity getByPlatUserId(String platUserId, String platId, Integer platType);

    /**
     * 根据用户id获取
     * @param id
     * @return
     */
    UserEntity getById(Integer id);
}
