package com.fox.api.dao.admin.mapper;

import com.fox.api.annotation.mapper.AdminMapperConfig;
import com.fox.api.dao.admin.entity.DateTypeEntity;

/**
 * 日期类型
 * @author lusongsong
 * @date 2020/10/05 16:49
 */
@AdminMapperConfig
public interface DateTypeMapper {
    /**
     * 插入
     * @param dateTypeEntity
     * @return
    */
    Integer insert(DateTypeEntity dateTypeEntity);

    /**
     * 更新
     * @param dateTypeEntity
     * @return
    */
    Boolean update(DateTypeEntity dateTypeEntity);

    /**
     * 根据日期类型查询
     * @param dt
     * @return
     */
    DateTypeEntity getByDate(String dt);
}
