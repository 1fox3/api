package com.fox.api.service.stock.api.request.impl;

import com.fox.api.entity.po.stock.api.StockRealtimeDealInfoPo;
import com.fox.api.service.stock.api.request.StockRealtimeDealInfoApiService;
import com.fox.api.service.stock.api.spider.StockRealtimeDealInfoSpiderApiService;
import com.fox.spider.stock.entity.vo.StockVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author lusongsong
 * @date 2021/1/15 17:39
 */
@Service
public class StockRealtimeDealInfoApiImpl
        extends StockApiServiceBaseImpl
        implements StockRealtimeDealInfoApiService {
    private Class spiderBeanClass = StockRealtimeDealInfoSpiderApiService.class;
    private StockVo demoStockVo;

    /**
     * 获取单只股票的实时交易信息
     *
     * @param stockVo
     * @return
     */
    @Override
    public StockRealtimeDealInfoPo realtimeDealInfo(StockVo stockVo) {
        demoStockVo = stockVo;
        Object object = getBean(spiderBeanClass);
        if (null != object) {
            return ((StockRealtimeDealInfoSpiderApiService) object).realtimeDealInfo(stockVo);
        }
        return null;
    }

    /**
     * 批量获取股票的实时交易信息
     *
     * @param stockVoList
     * @return
     */
    @Override
    public Map<String, StockRealtimeDealInfoPo> batchRealtimeDealInfo(List<StockVo> stockVoList) {
        if (null == stockVoList || stockVoList.isEmpty()) {
            return null;
        }
        demoStockVo = stockVoList.get(0);
        Object object = getBean(spiderBeanClass);
        if (null != object) {
            return ((StockRealtimeDealInfoSpiderApiService) object).batchRealtimeDealInfo(stockVoList);
        }
        return null;
    }

    /**
     * 判断bean是否适合
     *
     * @return
     */
    @Override
    public boolean verifyBean(Object object) {
        return ((StockRealtimeDealInfoSpiderApiService) object).isSupport(demoStockVo.getStockMarket());
    }
}
