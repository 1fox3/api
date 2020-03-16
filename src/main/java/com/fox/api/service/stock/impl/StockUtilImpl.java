package com.fox.api.service.stock.impl;

import com.fox.api.config.stock.StockConfig;
import com.fox.api.config.stock.entity.StockKindInfoEntity;
import com.fox.api.service.stock.StockUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class StockUtilImpl extends StockBaseImpl implements StockUtilService {
    @Autowired
    private StockConfig stockConfig;

    @Override
    public StockKindInfoEntity getStockKindInfo(String stockCode, Integer stockMarket) {
        StockKindInfoEntity stockKindInfo = new StockKindInfoEntity();
        Map<String, StockKindInfoEntity> map = stockConfig.getKind();
        for (StockKindInfoEntity stockKindInfoEntity : map.values()) {
            if (null != stockMarket && stockKindInfoEntity.getStockMarket() == stockMarket) {
                List<String> preCodeList = stockKindInfoEntity.getPerCode();
                for (String preCode : preCodeList) {
                    if (stockCode.startsWith(preCode)) {
                        stockKindInfo = stockKindInfoEntity;
                        break;
                    }
                }
            }
            if (null != stockKindInfo.getStockKind()) {
                break;
            }
        }
        return stockKindInfo;
    }
}
