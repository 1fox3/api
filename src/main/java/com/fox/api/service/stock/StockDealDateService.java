package com.fox.api.service.stock;

import com.fox.api.dao.stock.entity.StockDealDateEntity;

import java.util.Map;

/**
 * 股市交易日期服务类
 *
 * @author lusongsong
 * @date 2020/11/26 13:40
 */
public interface StockDealDateService {
    /**
     * 非交易日
     */
    int DEAL_DATE_NO = 0;
    /**
     * 交易日
     */
    int DEAL_DATE_YES = 1;
    /**
     * 未锁定
     */
    int DATE_UNLOCKED = 0;
    /**
     * 已锁定
     */
    int DATE_LOCKED = 1;
    /**
     * 上一个交易日
     */
    String PRE = "pre";
    /**
     * 当前交易日
     */
    String LAST = "last";
    /**
     * 下一个交易日
     */
    String NEXT = "next";

    /**
     * 股市近3个交易日(上一个，当前，下一个)
     *
     * @param stockMarket
     * @return
     */
    Map<String, String> around(Integer stockMarket);
}
