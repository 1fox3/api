package com.fox.api.dao.user.mapper;

import com.fox.api.annotation.mapper.UserMapperConfig;
import com.fox.api.dao.user.entity.UserLoginEntity;
import org.springframework.data.relational.core.sql.In;

/**
 * 用户登录
 * @author lusongsong
 */
@UserMapperConfig
public interface UserLoginMapper {
    /**
     * 添加
     * @param userLoginEntity
     * @return
     */
    Integer insert(UserLoginEntity userLoginEntity);

    /**
     * 更新
     * @param userLoginEntity
     * @return
     */
    Integer update(UserLoginEntity userLoginEntity);

    /**
     * 根据登录id获取
     * @param id
     * @return
     */
    UserLoginEntity getById(Integer id);

    /**
     * 退出登录
     * @param userLoginEntity
     * @return
     */
    Integer logout(UserLoginEntity userLoginEntity);
}
