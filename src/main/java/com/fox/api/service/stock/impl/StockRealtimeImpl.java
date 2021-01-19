package com.fox.api.service.stock.impl;

import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.entity.dto.stock.realtime.StockRealtimeInfoDto;
import com.fox.api.service.stock.StockRealtimeService;
import com.fox.spider.stock.api.nets.NetsRealtimeMinuteKLineApi;
import com.fox.spider.stock.api.sina.SinaRealtimeDealInfoApi;
import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.po.nets.NetsRealtimeMinuteKLinePo;
import com.fox.spider.stock.entity.po.sina.SinaRealtimeDealInfoPo;
import com.fox.spider.stock.entity.vo.StockVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class StockRealtimeImpl extends StockBaseImpl implements StockRealtimeService {
    /**
     * 新浪实时交易数据接口
     */
    @Autowired
    SinaRealtimeDealInfoApi sinaRealtimeDealInfoApi;
    /**
     * 网易分钟线图
     */
    @Autowired
    NetsRealtimeMinuteKLineApi netsRealtimeMinuteKLineApi;

    /**
     * 获取实时信息
     *
     * @param stockId
     * @return
     */
    @Override
    public SinaRealtimeDealInfoPo info(Integer stockId) {
        if (null == stockId) {
            return null;
        }
        StockEntity stockEntity = getStockEntity(stockId);
        if (null == stockEntity) {
            return null;
        }

        String hashKey = redisRealtimeStockInfoHash + ":" + stockEntity.getStockMarket();
        if (stockRedisUtil.hHasKey(hashKey, stockEntity.getStockCode())) {
            return (SinaRealtimeDealInfoPo) stockRedisUtil.hGet(hashKey, stockEntity.getStockCode());
        }

        return sinaRealtimeDealInfoApi.realtimeDealInfo(
                new StockVo(stockEntity.getStockCode(), stockEntity.getStockMarket())
        );
    }

    /**
     * 获取实时线图
     *
     * @param stockId
     * @return
     */
    @Override
    public NetsRealtimeMinuteKLinePo line(Integer stockId) {
        String redisKey = redisRealtimeStockLineSingle + stockId;
        NetsRealtimeMinuteKLinePo netsRealtimeMinuteDealInfoPo = (NetsRealtimeMinuteKLinePo) stockRedisUtil.get(redisKey);
        if (null != netsRealtimeMinuteDealInfoPo) {
            return netsRealtimeMinuteDealInfoPo;
        }
        StockEntity stockEntity = getStockEntity(stockId);
        if (null == stockEntity) {
            return null;
        }
        netsRealtimeMinuteDealInfoPo = netsRealtimeMinuteKLineApi.realtimeMinuteKLine(
                new StockVo(stockEntity.getStockCode(), stockEntity.getStockMarket())
        );
        if (null != netsRealtimeMinuteDealInfoPo) {
            stockRedisUtil.set(redisKey, netsRealtimeMinuteDealInfoPo, Long.valueOf(5));
        }
        return netsRealtimeMinuteDealInfoPo;
    }

    /**
     * 获取重要指标信息
     *
     * @return
     */
    @Override
    public List<StockRealtimeInfoDto> topIndex() {
        List<StockRealtimeInfoDto> list = new LinkedList<>();
        List<StockVo> topIndexList = StockConst.stockMarketTopIndex(StockConst.SM_A);
        for (StockVo stockVo : topIndexList) {
            StockEntity stockEntity = stockMapper.getByStockCode(
                    stockVo.getStockCode(),
                    stockVo.getStockMarket()
            );
            if (null != stockEntity && null != stockEntity.getId()) {
                Integer stockId = stockEntity.getId();
                SinaRealtimeDealInfoPo sinaRealtimeDealInfoPo = info(stockId);
                StockRealtimeInfoDto stockRealtimeInfoDto = new StockRealtimeInfoDto();
                BeanUtils.copyProperties(sinaRealtimeDealInfoPo, stockRealtimeInfoDto);
                stockRealtimeInfoDto.setStockId(stockId);
                list.add(stockRealtimeInfoDto);
            }
        }
        return list;
    }

    /**
     * 获取增幅统计信息
     *
     * @param stockMarket
     * @return
     */
    @Override
    public Map<String, Integer> uptickRateStatistics(Integer stockMarket) {
        Object object = stockRedisUtil.get(stockRealtimeStockUptickRateStatistics + ":" + stockMarket);
        if (null == object) {
            return new HashMap<>(0);
        }
        return (Map<String, Integer>) object;
    }
}
