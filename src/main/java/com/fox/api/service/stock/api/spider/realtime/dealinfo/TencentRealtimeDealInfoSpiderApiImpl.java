package com.fox.api.service.stock.api.spider.realtime.dealinfo;

import com.fox.api.entity.po.stock.api.StockRealtimeDealInfoPo;
import com.fox.api.service.stock.api.spider.StockRealtimeDealInfoSpiderApiService;
import com.fox.spider.stock.api.tencent.TencentRealtimeDealInfoApi;
import com.fox.spider.stock.entity.po.tencent.TencentRealtimeDealInfoPo;
import com.fox.spider.stock.entity.vo.StockVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 腾讯网股票最新交易日交易信息接口对接
 * 
 * @author lusongsong
 * @date 2021/1/15 15:44
 */
@Service
public class TencentRealtimeDealInfoSpiderApiImpl implements StockRealtimeDealInfoSpiderApiService {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 腾讯网股票最新交易日交易信息接口
     */
    @Autowired
    TencentRealtimeDealInfoApi tencentRealtimeDealInfoApi;

    /**
     * 获取单只股票的实时交易信息
     *
     * @param stockVo
     * @return
     */
    @Override
    public StockRealtimeDealInfoPo realtimeDealInfo(StockVo stockVo) {
        try {
            return convertObj(tencentRealtimeDealInfoApi.realtimeDealInfo(stockVo));
        } catch (Exception e) {
            logger.error(stockVo.toString(), e);
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
        try {
            Map<String, TencentRealtimeDealInfoPo> tencentBatchResult =
                    tencentRealtimeDealInfoApi.batchRealtimeDealInfo(stockVoList);
            if (null != tencentBatchResult && !tencentBatchResult.isEmpty()) {
                Map<String, StockRealtimeDealInfoPo> batchResult = new HashMap<>(tencentBatchResult.size());
                for (String stockCode : tencentBatchResult.keySet()) {
                    batchResult.put(stockCode, convertObj(tencentBatchResult.get(stockCode)));
                }
                return batchResult;
            }
        } catch (Exception e) {
            logger.error(stockVoList.toString(), e);
        }
        return null;
    }

    /**
     * 是否支持该证券所
     *
     * @param stockMarket
     * @return
     */
    @Override
    public boolean isSupport(int stockMarket) {
        return TencentRealtimeDealInfoApi.isSupport(stockMarket);
    }

    /**
     * 权重
     *
     * @return
     */
    @Override
    public int weight() {
        return 1;
    }

    /**
     * 对象转换
     *
     * @param tencentRealtimeDealInfoPo
     * @return
     */
    private StockRealtimeDealInfoPo convertObj(TencentRealtimeDealInfoPo tencentRealtimeDealInfoPo) {
        if (tencentRealtimeDealInfoPo instanceof TencentRealtimeDealInfoPo) {
            StockRealtimeDealInfoPo stockRealtimeDealInfoPo = new StockRealtimeDealInfoPo();
            BeanUtils.copyProperties(tencentRealtimeDealInfoPo, stockRealtimeDealInfoPo);
            return stockRealtimeDealInfoPo;
        }
        return null;
    }
}
