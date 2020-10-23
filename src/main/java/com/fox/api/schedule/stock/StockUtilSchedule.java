package com.fox.api.schedule.stock;

import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.entity.dto.http.HttpResponseDto;
import com.fox.api.entity.po.third.stock.StockRealtimePo;
import com.fox.api.entity.property.stock.StockCodeProperty;
import com.fox.api.service.third.stock.sina.api.SinaRealtime;
import com.fox.api.util.DateUtil;
import com.fox.api.util.HttpUtil;
import com.fox.api.util.StockUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 股票工具类任务，提供基本信息
 * @author lusongsong
 * @date 2020/5/2 14:20
 */
@Component
public class StockUtilSchedule extends StockBaseSchedule {
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 香港交易所token匹配正则
     */
    Pattern hkStockMarketTokenPattern = Pattern.compile("^return \"(.*)\";$");
    /**
     * 寻找最近交易日的日期扫描范围
     */
    private static final Integer DATE_SCAN = 30;

    /**
     * 获取最新交易日,当发现交易日发生变化时，更新上个交易日
     */
    @LogShowTimeAnt
    public void getStockMarketLastDealDate() {
        SinaRealtime sinaRealtime = new SinaRealtime();
        List<StockCodeProperty> stockCodePropertyList = stockProperty.getDemoIndex();
        String currentDate = DateUtil.getCurrentDate();
        for (StockCodeProperty stockCodeProperty : stockCodePropertyList) {
            try {
                String lastDealDateCacheKey = StockUtil.lastDealDateCacheKey(
                        stockCodeProperty.getStockMarket()
                );
                String currentDealDate = (String)this.stockRedisUtil.get(lastDealDateCacheKey);
                //如果日期是今天则无需刷新
                if (null != currentDealDate && currentDealDate.equals(currentDate)) {
                    continue;
                }
                StockEntity stockEntity = stockMapper.getByStockCode(
                        stockCodeProperty.getStockCode(), stockCodeProperty.getStockMarket()
                );
                if (null != stockEntity && null != stockEntity.getStockCode()) {
                    StockRealtimePo stockRealtimeEntity = sinaRealtime.getRealtimeData(stockEntity);
                    if (null != stockRealtimeEntity) {
                        String lastDealDate = stockRealtimeEntity.getCurrentDate();
                        if (null != lastDealDate && !lastDealDate.equals("") && !lastDealDate.equals(currentDealDate)) {
                            logger.info(stockCodeProperty.getStockMarket() + ":" + lastDealDate);
                            //设置最新交易日期
                            this.stockRedisUtil.set(lastDealDateCacheKey, lastDealDate);
                            refreshPreDealDate(stockCodeProperty.getStockMarket(), currentDealDate, lastDealDate);
                            refreshNextDealDate(stockCodeProperty.getStockMarket(), lastDealDate);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    /**
     * 刷新上个交易日
     * @param stockMarket
     * @param preDealDate
     * @param lastDealDate
     */
    private void refreshPreDealDate(Integer stockMarket, String preDealDate, String lastDealDate) {
        if (null == preDealDate || 0 == preDealDate.length()) {
            preDealDate = getCloselyDealDate(lastDealDate, stockMarket, false);
        }
        this.stockRedisUtil.set(
                StockUtil.preDealDateCacheKey(stockMarket),
                null == preDealDate || 0 == preDealDate.length() ? "" : preDealDate
        );
    }

    /**
     * 刷新下个交易日
     * @param stockMarket
     * @param lastDealDate
     */
    private void refreshNextDealDate(Integer stockMarket, String lastDealDate) {
        String nextDealDate = getCloselyDealDate(lastDealDate, stockMarket, true);
        this.stockRedisUtil.set(
                StockUtil.nextDealDateCacheKey(stockMarket),
                null == nextDealDate || 0 == nextDealDate.length() ? "" : nextDealDate
        );
    }

    /**
     * 获取最近的交易日
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
            HttpUtil httpUtil = new HttpUtil();
            httpUtil.setUrl("https://sc.hkex.com.hk/TuniS/www.hkex.com.hk/Market-Data/Securities-Prices/Equities/Equities-Quote?sym=700&sc_lang=zh-cn");
            HttpResponseDto httpResponse = httpUtil.request();
            String[] strings = httpResponse.getContent().split("\n");
            for (String string : strings) {
                string = string.trim();
                // 现在创建 matcher 对象
                Matcher matcher = hkStockMarketTokenPattern.matcher(string);
                if (matcher.find() && !matcher.group(1).equals("chn")) {
                    stockRedisUtil.set(StockUtil.HK_STOCK_MARKET_TOKEN, matcher.group(1));
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }
}
