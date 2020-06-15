package com.fox.api.dao.admin.mapper;

import com.fox.api.annotation.mapper.AdminMapperConfig;
import com.fox.api.dao.admin.entity.SignalEntity;

import java.util.List;

/**
 * 信号
 * @author lusongsong 
 */
@AdminMapperConfig
public interface SignalMapper {
    /**
     * 插入
     * @param signalEntity
     * @return
     */
    Integer insert(SignalEntity signalEntity);

    /**
     * 查询列表
     * @param startId
     * @param num
     * @return
     */
    List<SignalEntity> getList(Integer startId, Integer num);
}
