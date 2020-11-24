package com.fox.api.schedule.stock;

import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.util.StockUtil;
import com.fox.spider.stock.api.sina.SinaRealtimeDealInfoApi;
import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.po.sina.SinaRealtimeDealInfoPo;
import com.fox.spider.stock.entity.vo.StockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 批量获取股票的实时信息数据
 *
 * @author lusongsong
 * @date 2020/3/27 17:52
 */
@Component
public class StockRealtimeInfoSchedule extends StockBaseSchedule {
    @Autowired
    SinaRealtimeDealInfoApi sinaRealtimeDealInfoApi;

    /**
     * 更新股票实时交易数据
     */
    public void syncStockRealtimeDealInfo() {
        Integer onceLimit = 200;
        String schedule = "StockRealtimeInfoSchedule:syncStockRealtimeDealInfo";
        for (Integer stockMarket : StockConst.SM_CODE_ALL) {
            if (!realtimeDealScheduleCanRun(stockMarket, schedule)) {
                continue;
            }
            String listCacheKey = redisStockList + ":" + stockMarket;
            String hashCacheKey = redisRealtimeStockInfoHash + ":" + stockMarket;
            Long stockListSize = stockRedisUtil.lSize(listCacheKey);
            Map<String, SinaRealtimeDealInfoPo> sinaRealtimeDealInfoPoMap;
            List<StockVo> stockVoList = new ArrayList<>(onceLimit);
            for (Long i = Long.valueOf(0); i < stockListSize; i += onceLimit) {
                List<Object> stockEntityList = stockRedisUtil.lRange(listCacheKey, i, i + onceLimit - 1);
                if (null == stockEntityList || stockEntityList.isEmpty()) {
                    continue;
                }
                stockVoList.clear();
                for (Object stockEntity : stockEntityList) {
                    stockVoList.add(new StockVo(
                            ((StockEntity) stockEntity).getStockCode(),
                            ((StockEntity) stockEntity).getStockMarket()
                    ));
                }
                if (stockVoList.isEmpty()) {
                    continue;
                }
                sinaRealtimeDealInfoPoMap = sinaRealtimeDealInfoApi.batchRealtimeDealInfo(stockVoList);
                if (null != sinaRealtimeDealInfoPoMap) {
                    stockRedisUtil.hPutAll(hashCacheKey, sinaRealtimeDealInfoPoMap);
                }
            }
        }
    }
}
