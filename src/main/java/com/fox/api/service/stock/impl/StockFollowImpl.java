package com.fox.api.service.stock.impl;

import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.dao.stock.entity.StockFollowEntity;
import com.fox.api.dao.stock.mapper.StockFollowMapper;
import com.fox.api.service.stock.StockFollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class StockFollowImpl extends StockBaseImpl implements StockFollowService {

    @Autowired
    StockFollowMapper stockFollowMapper;

    @Override
    public List<Map<String, Object>> getByUser(int userId) {
        List<StockFollowEntity> followList = stockFollowMapper.getByUser(userId);
        List<Map<String, Object>> followInfoList = new LinkedList<>();
        if (null == followList || followList.size() == 0) {
            return followInfoList;
        }
        for (StockFollowEntity stockFollowEntity : followList) {
            Map<String, Object> followInfo = new HashMap<>();
            int stockId = stockFollowEntity.getStockId();
            StockEntity stockEntity = this.getStockEntity(stockId);
            followInfo.put("stockId", stockId);
            followInfo.put("stockName", stockEntity.getStockName());
            followInfo.put("stockCode", stockEntity.getStockCode());
            followInfo.put("followTime", stockFollowEntity.getFollowTime());
            followInfoList.add(followInfo);
        }
        return followInfoList;
    }
}
