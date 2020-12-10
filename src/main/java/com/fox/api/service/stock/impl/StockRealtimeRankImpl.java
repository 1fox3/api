package com.fox.api.service.stock.impl;

import com.fox.api.entity.dto.stock.realtime.rank.StockRealtimeRankInfoDto;
import com.fox.api.entity.po.PageInfoPo;
import com.fox.api.service.stock.StockRealtimeRankService;
import com.fox.spider.stock.entity.po.sina.SinaRealtimeDealInfoPo;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 股票实时数据排行
 *
 * @author lusongsong
 */
@Service
public class StockRealtimeRankImpl extends StockBaseImpl implements StockRealtimeRankService {
    public String asc = "ASC";
    /**
     * 支持的排行分类
     * price:价格
     * uptickRate:涨幅
     * surgeRate:波动
     * dealNum:成交量
     * dealMoney:成交金额
     */
    public static final String RANK_TYPE_PRICE = "price";
    public static final String RANK_TYPE_UPTICK_RATE = "uptickRate";
    public static final String RANK_TYPE_SURGE_RATE = "surgeRate";
    public static final String RANK_TYPE_DEAL_NUM = "dealNum";
    public static final String RANK_TYPE_DEAL_MONEY = "dealMoney";

    /**
     * 支持的排行分类列表
     */
    private List<String> supportRankTypeList = Arrays.asList(
            StockRealtimeRankImpl.RANK_TYPE_PRICE,
            StockRealtimeRankImpl.RANK_TYPE_UPTICK_RATE,
            StockRealtimeRankImpl.RANK_TYPE_SURGE_RATE,
            StockRealtimeRankImpl.RANK_TYPE_DEAL_NUM,
            StockRealtimeRankImpl.RANK_TYPE_DEAL_MONEY
    );


    /**
     * 获取排行数据
     *
     * @param stockMarket
     * @param type
     * @param sortType
     * @param pageInfo
     * @return
     */
    @Override
    public List<StockRealtimeRankInfoDto> rank(Integer stockMarket, String type, String sortType, PageInfoPo pageInfo) {
        List<StockRealtimeRankInfoDto> list = new LinkedList<>();
        //排序类型错误直接返回空列表
        if (!supportRankTypeList.contains(type)) {
            return list;
        }
        //根据排序类型获取列表
        Integer start = (pageInfo.getPageNum() - 1) * pageInfo.getPageSize();
        Integer end = pageInfo.getPageNum() * pageInfo.getPageSize() - 1;
        Set<Object> set;
        String redisZSetKey = getRedisZSetKey(stockMarket, type);
        if (asc.equals(sortType)) {
            set = stockRedisUtil.zRangeWithScores(
                    redisZSetKey, (long) start, (long) end
            );
        } else {
            set = stockRedisUtil.zReverseRangeWithScores(
                    redisZSetKey, (long) start, (long) end
            );
        }

        if (set.size() <= 0) {
            return list;
        }

        //获取股票id列表
        List stockCodeList = new LinkedList();
        for (Object object : set) {
            String value = (String) ((DefaultTypedTuple) object).getValue();
            stockCodeList.add(value);
        }

        //获取股票实时信息
        List<Object> inaRealtimeDealInfoPoList = stockRedisUtil.hMultiGet(
                redisRealtimeStockInfoHash + ":" + stockMarket,
                stockCodeList
        );
        Map<String, SinaRealtimeDealInfoPo> sinaRealtimeDealInfoPoMap = new HashMap<>(inaRealtimeDealInfoPoList.size());
        for (Object sinaRealtimeDealInfoPo : inaRealtimeDealInfoPoList) {
            sinaRealtimeDealInfoPoMap.put(
                    ((SinaRealtimeDealInfoPo) sinaRealtimeDealInfoPo).getStockCode(),
                    (SinaRealtimeDealInfoPo) sinaRealtimeDealInfoPo
            );
        }

        //根据排序结果遍历补充数据
        for (Object object : set) {
            String stockCode = (String) ((DefaultTypedTuple) object).getValue();
            SinaRealtimeDealInfoPo sinaRealtimeDealInfoPo = sinaRealtimeDealInfoPoMap.get(stockCode);
            StockRealtimeRankInfoDto stockRealtimeRankInfoDto = new StockRealtimeRankInfoDto();
            stockRealtimeRankInfoDto.setStockMarket(sinaRealtimeDealInfoPo.getStockMarket());
            stockRealtimeRankInfoDto.setStockCode(sinaRealtimeDealInfoPo.getStockCode());
            stockRealtimeRankInfoDto.setStockName(sinaRealtimeDealInfoPo.getStockName());
            stockRealtimeRankInfoDto.setCurrentPrice(sinaRealtimeDealInfoPo.getCurrentPrice());
            stockRealtimeRankInfoDto.setUptickRate(sinaRealtimeDealInfoPo.getUptickRate());
            stockRealtimeRankInfoDto.setSurgeRate(sinaRealtimeDealInfoPo.getSurgeRate());
            stockRealtimeRankInfoDto.setDealNum(sinaRealtimeDealInfoPo.getDealNum());
            stockRealtimeRankInfoDto.setDealMoney(sinaRealtimeDealInfoPo.getDealMoney());
            list.add(stockRealtimeRankInfoDto);
        }

        return list;
    }

    /**
     * 获取排行的缓存key
     *
     * @param type
     * @return
     */
    public String getRedisZSetKey(Integer stockMarket, String type) {
        if (StockRealtimeRankImpl.RANK_TYPE_PRICE.equals(type)) {
            return redisRealtimeRankPriceZSet + ":" + stockMarket;
        }

        if (StockRealtimeRankImpl.RANK_TYPE_UPTICK_RATE.equals(type)) {
            return redisRealtimeRankUptickRateZSet + ":" + stockMarket;
        }

        if (StockRealtimeRankImpl.RANK_TYPE_SURGE_RATE.equals(type)) {
            return redisRealtimeRankSurgeRateZSet + ":" + stockMarket;
        }

        if (StockRealtimeRankImpl.RANK_TYPE_DEAL_NUM.equals(type)) {
            return redisRealtimeRankDealNumZSet + ":" + stockMarket;
        }

        if (StockRealtimeRankImpl.RANK_TYPE_DEAL_MONEY.equals(type)) {
            return redisRealtimeRankDealMoneyZSet + ":" + stockMarket;
        }
        return redisRealtimeRankUptickRateZSet + ":" + stockMarket;
    }
}
