package com.fox.api.service.stock.api.request.impl;

import com.fox.api.entity.po.stock.api.StockRealtimeMinuteKLinePo;
import com.fox.api.service.stock.api.request.StockRealtimeMinuteKLineApiService;
import com.fox.api.service.stock.api.spider.StockRealtimeMinuteKLineSpiderApiService;
import com.fox.spider.stock.entity.vo.StockVo;
import org.springframework.stereotype.Service;

/**
 * 股票最新交易日分钟线图数据
 *
 * @author lusongsong
 * @date 2021/1/22 16:42
 */
@Service
public class StockRealtimeMinuteKLineApiImpl
        extends StockApiServiceBaseImpl
        implements StockRealtimeMinuteKLineApiService {
    /**
     * 股票最新交易日分钟线图数据
     */
    private Class spiderBeanClass = StockRealtimeMinuteKLineSpiderApiService.class;

    /**
     * 无参构造函数
     */
    StockRealtimeMinuteKLineApiImpl() {
        super();
        chooseMethod = StockApiServiceBaseImpl.CHOOSE_METHOD_POLL;
    }

    /**
     * 股票最新交易日分钟线图数据
     *
     * @param stockVo
     * @return
     */
    @Override
    public StockRealtimeMinuteKLinePo realtimeMinuteKLine(StockVo stockVo) {
        demoStockVo = stockVo;
        Object object = getBean(spiderBeanClass);
        if (null != object) {
            return ((StockRealtimeMinuteKLineSpiderApiService) object).realtimeMinuteKLine(stockVo);
        }
        return null;
    }
}
