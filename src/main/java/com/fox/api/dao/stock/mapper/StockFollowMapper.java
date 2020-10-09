package com.fox.api.dao.stock.mapper;

import com.fox.api.annotation.mapper.StockMapperConfig;
import com.fox.api.dao.stock.entity.StockFollowEntity;

import java.util.List;

/**
 * 关注表
 * @author lusongsong
 * @date 2020/3/5 18:13
 */
@StockMapperConfig
public interface StockFollowMapper {
    /**
     * 根据用户获取关注列表
     * @param userId
     * @return
     */
    List<StockFollowEntity> getByUser(int userId);
}
