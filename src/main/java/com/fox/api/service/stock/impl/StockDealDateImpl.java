package com.fox.api.service.stock.impl;

import com.fox.api.dao.stock.mapper.StockDealDateMapper;
import com.fox.api.service.stock.StockDealDateService;
import com.fox.api.util.StockUtil;
import com.fox.spider.stock.constant.StockConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 股市交易日服务类
 *
 * @author lusongsong
 * @date 2020/11/26 13:41
 */
@Service
public class StockDealDateImpl implements StockDealDateService {
    /**
     * 股市交替日期数据库操作类
     */
    @Autowired
    StockDealDateMapper stockDealDateMapper;

    /**
     * 股市近3个交易日(上一个，当前，下一个)
     *
     * @param stockMarket
     * @return
     */
    @Override
    public Map<String, String> around(Integer stockMarket) {
        if (null == stockMarket || !StockConst.SM_ALL.contains(stockMarket)) {
            return null;
        }
        Map<String, String> dealDateMap = new HashMap<>(3);
        dealDateMap.put(PRE, StockUtil.preDealDate(stockMarket));
        dealDateMap.put(LAST, StockUtil.lastDealDate(stockMarket));
        dealDateMap.put(NEXT, StockUtil.nextDealDate(stockMarket));
        return dealDateMap;
    }
}
