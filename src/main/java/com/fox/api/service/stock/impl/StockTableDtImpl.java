package com.fox.api.service.stock.impl;

import com.fox.api.constant.stock.StockTableDtConst;
import com.fox.api.dao.stock.entity.StockTableDtEntity;
import com.fox.api.dao.stock.mapper.StockTableDtMapper;
import com.fox.api.service.stock.StockTableDtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据表数据日期管理
 *
 * @author lusongsong
 * @date 2020/10/30 17:18
 */
@Service
public class StockTableDtImpl implements StockTableDtService {
    @Autowired
    StockTableDtMapper stockTableDtMapper;

    /**
     * 添加
     *
     * @param stockTableDtEntity
     * @return
     */
    @Override
    public Integer insert(StockTableDtEntity stockTableDtEntity) {
        if (null == stockTableDtEntity) {
            return null;
        }
        StockTableDtEntity dbStockTableDtEntity = stockTableDtMapper.getByTableDt(stockTableDtEntity);
        if (null != dbStockTableDtEntity && null != dbStockTableDtEntity.getId()) {
            stockTableDtEntity.setId(dbStockTableDtEntity.getId());
            if (stockTableDtMapper.update(stockTableDtEntity)) {
                return dbStockTableDtEntity.getId();
            } else {
                return null;
            }
        } else {
            return stockTableDtMapper.insert(stockTableDtEntity);
        }
    }

    /**
     * 是指已被备份
     *
     * @param stockTableDtEntity
     * @return
     */
    @Override
    public Boolean setBak(StockTableDtEntity stockTableDtEntity) {
        if (null == stockTableDtEntity) {
            return false;
        }
        stockTableDtEntity.setTable(StockTableDtConst.TYPE_BAK);
        return stockTableDtMapper.update(stockTableDtEntity);
    }

    /**
     * 根据类型获取日期列表
     *
     * @param stockTableDtEntity
     * @return
     */
    @Override
    public List<String> getByType(StockTableDtEntity stockTableDtEntity) {
        if (null == stockTableDtEntity) {
            return null;
        }
        return stockTableDtMapper.getDtByType(stockTableDtEntity);
    }

    /**
     * 根据数据表和日期查询记录
     *
     * @param stockTableDtEntity
     * @return
     */
    @Override
    public StockTableDtEntity getByTableDt(StockTableDtEntity stockTableDtEntity) {
        if (null == stockTableDtEntity) {
            return null;
        }
        return stockTableDtMapper.getByTableDt(stockTableDtEntity);
    }
}
