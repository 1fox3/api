package com.fox.api.dao.user.mapper;

import com.fox.api.annotation.mapper.UserMapperConfig;
import com.fox.api.dao.user.entity.StockHelperUserInfoEntity;

/**
 * 股票助手用户
 * @author lusongsong 
 */
@UserMapperConfig
public interface StockHelperUserInfoMapper {
    /**
     * 插入
     * @param stockHelperUserInfoEntity
     * @return
     */
    Integer insert(StockHelperUserInfoEntity stockHelperUserInfoEntity);


    /**
     * 更新
     * @param stockHelperUserInfoEntity
     * @return
    */
    Boolean update(StockHelperUserInfoEntity stockHelperUserInfoEntity);

    /**
     * 根据账号获取信息
     * @param account
     * @return
     */
    StockHelperUserInfoEntity getByAccount(String account);
}
