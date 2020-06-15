package com.fox.api.service.admin.impl;

import com.fox.api.dao.admin.entity.SignalEntity;
import com.fox.api.dao.admin.mapper.SignalMapper;
import com.fox.api.entity.po.PageInfoPo;
import com.fox.api.service.admin.Signal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 信号管理
 * @author lusongsong
 */
@Service
public class SignalImpl implements Signal {
    @Autowired
    private SignalMapper signalMapper;

    /**
     * 添加信号
     *
     * @param signalEntity
     * @return
     */
    @Override
    public Integer insert(SignalEntity signalEntity) {
        return signalMapper.insert(signalEntity);
    }

    /**
     * 查询列表
     *
     * @param startId
     * @param pageInfoPo
     * @return
     */
    @Override
    public List<SignalEntity> getList(Integer startId, PageInfoPo pageInfoPo) {
        return signalMapper.getList(startId, pageInfoPo.getPageSize());
    }
}
