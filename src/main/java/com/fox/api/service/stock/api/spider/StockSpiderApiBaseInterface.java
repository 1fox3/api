package com.fox.api.service.stock.api.spider;

/**
 * 股票基本接口
 *
 * @author lusongsong
 * @date 2021/1/15 15:02
 */
public interface StockSpiderApiBaseInterface {
    /**
     * 是否支持该证券所
     *
     * @param stockMarket
     * @return
     */
    boolean isSupport(int stockMarket);

    /**
     * 权重
     * @return
     */
    int weight();
}
