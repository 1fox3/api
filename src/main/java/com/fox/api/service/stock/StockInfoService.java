package com.fox.api.service.stock;

import com.fox.api.dao.stock.entity.StockInfoEntity;

import java.util.List;
import java.util.Map;

/**
 * 股票信息
 * @author lusongsong
 */
public interface StockInfoService {
    /**
     * 从交易所获取股票信息
     * @param stockId
     * @return
     */
    StockInfoEntity getInfoFromStockExchange(Integer stockId);

    /**
     * 查询股票信息
     * @param stockId
     * @return
     */
    StockInfoEntity getInfo(Integer stockId);

    /**
     * 搜索
     * @param search
     * @return
     */
    List<Map<String, Object>> search(String search);
}
