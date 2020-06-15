package com.fox.api.service.admin;

import com.fox.api.dao.admin.entity.SignalEntity;
import com.fox.api.entity.po.PageInfoPo;

import java.util.List;

/**
 * 信号管理
 * @author lusongsong
 */
public interface Signal {
    /**
     * 添加信号
     * @param signalEntity
     * @return
     */
    Integer insert(SignalEntity signalEntity);

    /**
     * 查询列表
     * @param startId
     * @param pageInfoPo
     * @return
     */
    List<SignalEntity> getList(Integer startId, PageInfoPo pageInfoPo);
}
