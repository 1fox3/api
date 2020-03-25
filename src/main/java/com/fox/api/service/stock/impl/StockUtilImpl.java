package com.fox.api.service.stock.impl;

import com.fox.api.property.stock.StockProperty;
import com.fox.api.entity.property.stock.StockKindInfoProperty;
import com.fox.api.service.stock.StockUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class StockUtilImpl extends StockBaseImpl implements StockUtilService {
    @Autowired
    private StockProperty stockConfig;

    @Override
    public StockKindInfoProperty getStockKindInfo(String stockCode, Integer stockMarket) {
        StockKindInfoProperty stockKindInfo = new StockKindInfoProperty();
        Map<String, StockKindInfoProperty> map = stockConfig.getKind();
        for (StockKindInfoProperty stockKindInfoEntity : map.values()) {
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
