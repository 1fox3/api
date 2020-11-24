package com.fox.api.schedule.stock;

import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
import com.fox.api.util.DateUtil;
import com.fox.api.util.StockUtil;
import com.fox.spider.stock.api.hk.HKStockInfoApi;
import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.constant.StockMarketStatusConst;
import com.fox.spider.stock.service.StockToolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;

/**
 * 股票工具类任务，提供基本信息
 *
 * @author lusongsong
 * @date 2020/5/2 14:20
 */
@Component
public class StockUtilSchedule extends StockBaseSchedule {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 新浪实时交易信息
     */
    @Autowired
    StockToolService stockToolService;
    /**
     * 港股股票信息类
     */
    @Autowired
    HKStockInfoApi hkStockInfoApi;
    /**
     * 寻找最近交易日的日期扫描范围
     */
    private static final Integer DATE_SCAN = 30;

    /**
     * 获取最新交易日,当发现交易日发生变化时，更新上个交易日
     * A股一般在9:05更新
     * 港股在9:35更新
     */
    public void syncStockMarketLastDealDate(Integer stockMarket) {
        if (StockConst.SM_ALL.contains(stockMarket)) {
            String currentDate = DateUtil.getCurrentDate();
            try {
                String currentDealDate = StockUtil.lastDealDate(stockMarket);
                //如果日期是今天则无需刷新
                if (null != currentDealDate && currentDealDate.equals(currentDate)) {
                    return;
                }
                String lastDealDate = stockToolService.lastDealDate(stockMarket);
                if (null != lastDealDate && !lastDealDate.equals("") && !lastDealDate.equals(currentDealDate)) {
                    logger.error(stockMarket + ":" + lastDealDate);
                    //设置最新交易日期
                    stockRedisUtil.set(StockUtil.lastDealDateCacheKey(stockMarket), lastDealDate);
                    refreshPreDealDate(stockMarket, currentDealDate, lastDealDate);
                    refreshNextDealDate(stockMarket, lastDealDate);
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    /**
     * 刷新上个交易日
     *
     * @param stockMarket
     * @param preDealDate
     * @param lastDealDate
     */
    private void refreshPreDealDate(Integer stockMarket, String preDealDate, String lastDealDate) {
        if (null == preDealDate || 0 == preDealDate.length()) {
            preDealDate = getCloselyDealDate(lastDealDate, stockMarket, false);
        }
        stockRedisUtil.set(
                StockUtil.preDealDateCacheKey(stockMarket),
                null == preDealDate || 0 == preDealDate.length() ? "" : preDealDate
        );
    }

    /**
     * 刷新下个交易日
     *
     * @param stockMarket
     * @param lastDealDate
     */
    private void refreshNextDealDate(Integer stockMarket, String lastDealDate) {
        String nextDealDate = getCloselyDealDate(lastDealDate, stockMarket, true);
        stockRedisUtil.set(
                StockUtil.nextDealDateCacheKey(stockMarket),
                null == nextDealDate || 0 == nextDealDate.length() ? "" : nextDealDate
        );
    }

    /**
     * 获取最近的交易日
     *
     * @param dealDate
     * @param stockMarket
     * @param isFuture
     * @return
     */
    private String getCloselyDealDate(String dealDate, Integer stockMarket, Boolean isFuture) {
        String currentDate = "";
        for (int i = 1; i < DATE_SCAN; i++) {
            try {
                currentDate = DateUtil.getRelateDate(
                        dealDate, 0, 0, isFuture ? i : -i, DateUtil.DATE_FORMAT_1
                );
                if (isDealDate(stockMarket, currentDate)) {
                    break;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return currentDate;
    }

    /**
     * 刷新香港交易所token
     */
    @LogShowTimeAnt
    public void hkStockMarketToken() {
        try {
            String token = hkStockInfoApi.apiToken();
            if (null != token && !token.isEmpty()) {
                stockRedisUtil.set(StockUtil.HK_STOCK_MARKET_TOKEN, token);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 同步股市交易状态
     */
    public void syncSMDealStatus() {
        for (Integer stockMarket : StockConst.SM_ALL) {
            int dealStatus = StockMarketStatusConst.REST;
            if (StockUtil.todayIsDealDate(stockMarket)) {
                dealStatus = StockMarketStatusConst.timeSMStatus(stockMarket);
            }
            stockRedisUtil.set(StockUtil.SM_DEAL_STATUS + ":" + stockMarket, dealStatus);
        }
    }
}
