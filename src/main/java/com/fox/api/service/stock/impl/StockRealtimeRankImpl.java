package com.fox.api.service.stock.impl;

import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.entity.dto.stock.realtime.rank.StockRealtimeRankInfoDto;
import com.fox.api.entity.po.PageInfoPo;
import com.fox.api.entity.po.third.stock.StockRealtimePo;
import com.fox.api.service.stock.StockRealtimeRankService;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 股票实时数据排行
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
     * @param type
     * @param pageInfo
     * @return
     */
    @Override
    public List<StockRealtimeRankInfoDto> rank(String type, String sortType, PageInfoPo pageInfo) {
        List<StockRealtimeRankInfoDto> list = new LinkedList<>();
        //排序类型错误直接返回空列表
        if (!this.supportRankTypeList.contains(type)) {
            return list;
        }
        //根据排序类型获取列表
        Integer start = (pageInfo.getPageNum() - 1) * pageInfo.getPageSize();
        Integer end = pageInfo.getPageNum() * pageInfo.getPageSize() - 1;
        Set<Object> set;
        String redisZSetKey = this.getRedisZSetKey(type);
        if (this.asc.equals(sortType)) {
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

        //获取股票id列表
        List stockIdList = new LinkedList();
        for(Object object : set) {
            Integer value = (Integer) ((DefaultTypedTuple)object).getValue();
            stockIdList.add(value.toString());
        }

        //获取股票实时信息
        List<Object> stockRealtimePoList = this.stockRedisUtil.hMultiGet(this.redisRealtimeStockInfoHash, stockIdList);
        Map<Integer, StockRealtimePo> stockRealtimePoMap = new HashMap<>(stockRealtimePoList.size());
        for (Object stockRealtimePo : stockRealtimePoList) {
            stockRealtimePoMap.put(
                    ((StockRealtimePo) stockRealtimePo).getStockId(),
                    (StockRealtimePo) stockRealtimePo
            );
        }

        //根据排序结果遍历补充数据
        for(Object object : set) {
            Integer stockId = (Integer)((DefaultTypedTuple)object).getValue();
            StockRealtimePo stockRealtimePo = stockRealtimePoMap.get(stockId);
            StockRealtimeRankInfoDto stockRealtimeRankInfoDto = new StockRealtimeRankInfoDto();
            stockRealtimeRankInfoDto.setStockId(stockRealtimePo.getStockId());
            stockRealtimeRankInfoDto.setStockCode(stockRealtimePo.getStockCode());
            stockRealtimeRankInfoDto.setStockName(stockRealtimePo.getStockName());
            stockRealtimeRankInfoDto.setPrice((double)stockRealtimePo.getCurrentPrice());
            stockRealtimeRankInfoDto.setUptickRate((double)stockRealtimePo.getUptickRate());
            stockRealtimeRankInfoDto.setSurgeRate((double)stockRealtimePo.getSurgeRate());
            stockRealtimeRankInfoDto.setDealNum((double)stockRealtimePo.getDealNum());
            stockRealtimeRankInfoDto.setDealMoney((double)stockRealtimePo.getDealMoney());
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
        if (StockRealtimeRankImpl.RANK_TYPE_PRICE.equals(type)) {
            return this.redisRealtimeRankPriceZSet;
        }

        if (StockRealtimeRankImpl.RANK_TYPE_UPTICK_RATE.equals(type)) {
            return this.redisRealtimeRankUptickRateZSet;
        }

        if (StockRealtimeRankImpl.RANK_TYPE_SURGE_RATE.equals(type)) {
            return this.redisRealtimeRankSurgeRateZSet;
        }

        if (StockRealtimeRankImpl.RANK_TYPE_DEAL_NUM.equals(type)) {
            return this.redisRealtimeRankDealNumZSet;
        }

        if (StockRealtimeRankImpl.RANK_TYPE_DEAL_MONEY.equals(type)) {
            return this.redisRealtimeRankDealMoneyZSet;
        }
        return this.redisRealtimeRankUptickRateZSet;
    }
}
