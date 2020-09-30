package com.fox.api.schedule.stock;

import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.entity.po.third.stock.StockRealtimePo;
import com.fox.api.service.third.stock.sina.api.SinaRealtime;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 批量获取股票的实时信息数据
 * @author lusongsong
 * @date 2020/3/27 17:52
 */
@Component
public class StockRealtimeInfoSchedule extends StockBaseSchedule {

    /**
     * 获取实时交易信息
     */
    @LogShowTimeAnt
    public void stockRealtimeInfo() {
        Integer onceLimit = 200;
        Long stockListSize = this.stockRedisUtil.lSize(this.redisStockList);
        Map<String, StockRealtimePo> stockRealtimePoMap = new HashMap<>(onceLimit);
        SinaRealtime sinaRealtime = new SinaRealtime();
        for (Long i = Long.valueOf(0); i < stockListSize; i += onceLimit) {
            List<Object> stockEntityList = this.stockRedisUtil.lRange(this.redisStockList, i, i + onceLimit - 1);
            if (null == stockEntityList || 0 >= stockEntityList.size()) {
                continue;
            }
            //3个重要指数也更新
            if (0 == i) {
                List<Integer> topIndexList = this.stockProperty.getTopIndex();
                for (Integer stockId : topIndexList) {
                    stockEntityList.add(this.stockMapper.getById(stockId));
                }
            }
            List<String> sinaStockCodeList = new ArrayList<>();
            for (Object stockEntity : stockEntityList) {
                sinaStockCodeList.add(((StockEntity)stockEntity).getSinaStockCode());
            }
            if (0 >= sinaStockCodeList.size()) {
                continue;
            }
            Map<String, StockRealtimePo> sinaStockRealtimePoMap = sinaRealtime.getRealtimeData(sinaStockCodeList);
            stockRealtimePoMap.clear();
            for (Object stockEntity : stockEntityList) {
                Integer stockId = ((StockEntity)stockEntity).getId();
                String sinaStockCode = ((StockEntity)stockEntity).getSinaStockCode();
                if (null != stockId && null != sinaStockCode && sinaStockRealtimePoMap.containsKey(sinaStockCode)) {
                    StockRealtimePo stockRealtimePo = sinaStockRealtimePoMap.get(sinaStockCode);
                    stockRealtimePo.setStockId(((StockEntity) stockEntity).getId());
                    stockRealtimePo.setStockCode(((StockEntity) stockEntity).getStockCode());
                    stockRealtimePoMap.put(String.valueOf(stockId), stockRealtimePo);
                }
            }
            this.stockRedisUtil.hPutAll(this.redisRealtimeStockInfoHash, stockRealtimePoMap);
        }
    }
}
