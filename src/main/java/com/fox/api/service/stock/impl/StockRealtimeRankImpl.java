package com.fox.api.service.stock.impl;

import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.entity.dto.stock.realtime.rank.StockRealtimeRankInfoDto;
import com.fox.api.entity.po.PageInfoPo;
import com.fox.api.service.stock.StockRealtimeRankService;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 股票实时数据排行
 */
@Service
public class StockRealtimeRankImpl extends StockBaseImpl implements StockRealtimeRankService {
    /**
     * 支持的排行分类
     */
    private List<String> supportRankTypeList = Arrays.asList(
            "uptickRate", //涨幅
            "surgeRate", //波动
            "dealNum", //成交量
            "dealMoney" //成交金额
    );

    /**
     * 获取排行数据
     * @param type
     * @param pageInfo
     * @return
     */
    public List<StockRealtimeRankInfoDto> rank(String type, String sortType, PageInfoPo pageInfo) {
        List<StockRealtimeRankInfoDto> list = new LinkedList<>();
        if (!this.supportRankTypeList.contains(type)) {
            return list;
        }
        Integer start = (pageInfo.getPageNum() - 1) * pageInfo.getPageSize();
        Integer end = pageInfo.getPageNum() * pageInfo.getPageSize() - 1;
        Set<Object> set;
        String redisZSetKey = this.getRedisZSetKey(type);
        if ("ASC".equals(sortType)) {
            set = this.stockRedisUtil.zRangeWithScores(
                    redisZSetKey, (long)start, (long)end
            );
        } else {
            set = this.stockRedisUtil.zReverseRangeWithScores(
                    redisZSetKey, (long)start, (long)end
            );
        }

        if (set.size() <= 0) {
            return list;
        }
        Map<Integer, Double> scoreMap = new HashMap<>();
        List stockIdList = new LinkedList();
        for(Object object : set) {
            Integer value = (Integer) ((DefaultTypedTuple)object).getValue();
            Double score = ((DefaultTypedTuple)object).getScore();
            stockIdList.add(value.toString());
            scoreMap.put(value, score);
        }

        List<Object> stockEntityList = this.stockRedisUtil.hMultiGet(this.redisStockHash, stockIdList);
        for (Object stockEntity : stockEntityList) {
            StockRealtimeRankInfoDto stockRealtimeRankInfoDto = new StockRealtimeRankInfoDto();
            stockRealtimeRankInfoDto.setStockId(((StockEntity) stockEntity).getId());
            stockRealtimeRankInfoDto.setStockCode(((StockEntity) stockEntity).getStockCode());
            stockRealtimeRankInfoDto.setStockName(((StockEntity) stockEntity).getStockName());
            if ("uptickRate".equals(type)) {
                stockRealtimeRankInfoDto.setUptickRate(
                        Double.valueOf(scoreMap.get(((StockEntity) stockEntity).getId()))
                );
            }

            if ("surgeRate".equals(type)) {
                stockRealtimeRankInfoDto.setSurgeRate(
                        Double.valueOf(scoreMap.get(((StockEntity) stockEntity).getId()))
                );
            }

            if ("dealNum".equals(type)) {
                stockRealtimeRankInfoDto.setDealNum(
                        Double.valueOf(scoreMap.get(((StockEntity) stockEntity).getId()))
                );
            }

            if ("dealMoney".equals(type)) {
                stockRealtimeRankInfoDto.setDealMoney(
                        Double.valueOf(scoreMap.get(((StockEntity) stockEntity).getId()))
                );
            }

            list.add(stockRealtimeRankInfoDto);
        }

        return list;
    }

    /**
     * 获取排行的缓存key
     * @param type
     * @return
     */
    public String getRedisZSetKey(String type) {
        if ("uptickRate".equals(type)) {
            return this.redisRealtimeRankUptickRateZSet;
        }

        if ("surgeRate".equals(type)) {
            return this.redisRealtimeRankSurgeRateZSet;
        }

        if ("dealNum".equals(type)) {
            return this.redisRealtimeRankDealNumZSet;
        }

        if ("dealMoney".equals(type)) {
            return this.redisRealtimeRankDealMoneyZSet;
        }
        return this.redisRealtimeRankUptickRateZSet;
    }
}
