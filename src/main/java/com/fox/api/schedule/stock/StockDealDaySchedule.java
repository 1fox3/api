package com.fox.api.schedule.stock;

import com.fox.api.dao.stock.entity.StockDealDayEntity;
import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.dao.stock.mapper.StockDealDayMapper;
import com.fox.api.entity.po.third.stock.StockDayLinePo;
import com.fox.api.entity.po.third.stock.StockDealPo;
import com.fox.api.entity.po.third.stock.StockRealtimePo;
import com.fox.api.service.third.stock.nets.api.NetsDayLine;
import com.fox.api.util.DateUtil;
import com.fox.api.util.StockUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class StockDealDaySchedule extends StockBaseSchedule {
    @Autowired
    private StockDealDayMapper stockDealDayMapper;

    Map<String, Integer> fqTypeMap = new LinkedHashMap<String, Integer>(){{
        put("kline", 0);
        put("klinederc", 1);
    }};

    /**
     * 同步所有的按天交易信息数据
     */
    @Scheduled(cron="0 30 16 * * 1-5")
    public void syncTotalDealDayInfo() {
        //截断表
        stockDealDayMapper.truncate();
        //优化表空间
        stockDealDayMapper.optimize();
        Integer startYear = 1990;
        Integer endYear = Integer.valueOf(DateUtil.getCurrentYear());
        NetsDayLine netsDayLine = new NetsDayLine();
        Integer onceLimit = 200;
        Long stockListSize = this.stockRedisUtil.lSize(this.redisStockList);
        for (Long i = Long.valueOf(0); i < stockListSize; i += onceLimit) {
            List<Object> stockEntityList = this.stockRedisUtil.lRange(this.redisStockList, i, i + onceLimit - 1);
            if (null == stockEntityList || 0 >= stockEntityList.size()) {
                continue;
            }
            for (Object object : stockEntityList) {
                for (String fqType : this.fqTypeMap.keySet()) {
                    for (Integer year = startYear; year <= endYear; year += 1) {
                        String startDate = year + "-01-01";
                        String endDate = year + "-12-31";
                        Map<String, String> netsParams = StockUtil.getNetsStockInfoMap((StockEntity) object);
                        netsParams.put("rehabilitationType", fqType);
                        StockDayLinePo stockDayLinePo = netsDayLine.getDayLine(netsParams, startDate, endDate);
                        if (null == stockDayLinePo.getLineNode()) {
                            continue;
                        }
                        List<StockDealDayEntity> list = new LinkedList<>();
                        for (StockDealPo stockDealPo : stockDayLinePo.getLineNode()) {
                            StockDealDayEntity stockDealDayEntity = new StockDealDayEntity();
                            stockDealDayEntity.setStockId(((StockEntity) object).getId());
                            stockDealDayEntity.setDt(stockDealPo.getDateTime());
                            stockDealDayEntity.setFqType(this.fqTypeMap.get(fqType));
                            stockDealDayEntity.setOpenPrice(stockDealPo.getOpenPrice());
                            stockDealDayEntity.setClosePrice(stockDealPo.getClosePrice());
                            stockDealDayEntity.setHighestPrice(stockDealPo.getHighestPrice());
                            stockDealDayEntity.setLowestPrice(stockDealPo.getLowestPrice());
                            stockDealDayEntity.setDealNum(stockDealPo.getDealNum());
                            stockDealDayEntity.setUptickRate(stockDealPo.getUptickRate());
                            list.add(stockDealDayEntity);
                        }
                        if (list.size() > 0 ){
                            stockDealDayMapper.batchInsert(list);
                            try{
                                Thread.sleep(100);
                            } catch (InterruptedException e){}
                        }
                        System.out.println(((StockEntity) object).getId());
                    }
                }
            }
        }
    }

    /**
     * 获取成交金额
     * @param stockId
     * @return
     */
    private Double getDealMoney(Integer stockId) {
        StockRealtimePo stockRealtimePo = new StockRealtimePo();
        if (this.stockRedisUtil.hHasKey(this.redisRealtimeStockInfoHash, stockId.toString())) {
            stockRealtimePo = (StockRealtimePo)this.stockRedisUtil.hGet(this.redisRealtimeStockInfoHash, stockId.toString());
        }
        return stockRealtimePo.getDealMoney();
    }

    /**
     * 同步当天的交易
     */
//    @Scheduled(cron="0 5 15 * * 1-5")
    public void syncCurrentDealDayInfo() {
        NetsDayLine netsDayLine = new NetsDayLine();
        Integer onceLimit = 200;
        Long stockListSize = this.stockRedisUtil.lSize(this.redisStockList);
        for (Long i = Long.valueOf(0); i < stockListSize; i += onceLimit - 1) {
            List<Object> stockEntityList = this.stockRedisUtil.lRange(this.redisStockList, i, i + onceLimit - 1);
            if (null == stockEntityList || 0 >= stockEntityList.size()) {
                continue;
            }
            for (Object object : stockEntityList) {
                Double dealMoney = this.getDealMoney(((StockEntity) object).getId());
                for (String fqType : this.fqTypeMap.keySet()) {
                    String startDate = DateUtil.getCurrentDate();
                    String endDate = startDate;
                    Map<String, String> netsParams = StockUtil.getNetsStockInfoMap((StockEntity) object);
                    netsParams.put("rehabilitationType", fqType);
                    StockDayLinePo stockDayLinePo = netsDayLine.getDayLine(netsParams, startDate, endDate);
                    if (null == stockDayLinePo.getLineNode()) {
                        continue;
                    }
                    List<StockDealDayEntity> list = new LinkedList<>();
                    for (StockDealPo stockDealPo : stockDayLinePo.getLineNode()) {
                        StockDealDayEntity stockDealDayEntity = new StockDealDayEntity();
                        stockDealDayEntity.setStockId(((StockEntity) object).getId());
                        stockDealDayEntity.setDt(stockDealPo.getDateTime());
                        stockDealDayEntity.setFqType(this.fqTypeMap.get(fqType));
                        stockDealDayEntity.setOpenPrice(stockDealPo.getOpenPrice());
                        stockDealDayEntity.setClosePrice(stockDealPo.getClosePrice());
                        stockDealDayEntity.setHighestPrice(stockDealPo.getHighestPrice());
                        stockDealDayEntity.setLowestPrice(stockDealPo.getLowestPrice());
                        stockDealDayEntity.setDealNum(stockDealPo.getDealNum());
                        if (null != dealMoney) {
                            stockDealDayEntity.setDealMoney(dealMoney);
                        }
                        stockDealDayEntity.setUptickRate(stockDealPo.getUptickRate());
                        list.add(stockDealDayEntity);
                    }
                    if (list.size() > 0 ){
                        stockDealDayMapper.batchInsert(list);
                        try{
                            Thread.sleep(100);
                        } catch (InterruptedException e){}
                    }
                    System.out.println(((StockEntity) object).getId());
                }
            }
        }
    }
}
