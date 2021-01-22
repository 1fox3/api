package com.fox.api.service.stock.api.request;

/**
 * 股票接口服务基础接口
 *
 * @author lusongsong
 * @date 2021/1/22 16:41
 */
public interface StockBaseApiService {
    /**
     * 设置选取策略
     *
     * @param method
     */
    void setChooseMethod(int method);
}
