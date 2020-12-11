package com.fox.api.service.stock.dealinfo.impl;

import com.fox.api.entity.po.stock.dealinfo.StockRealtimeMinuteDealInfoPo;
import com.fox.api.entity.po.stock.dealinfo.StockRealtimeMinuteNodeDealInfoPo;
import com.fox.api.service.stock.dealinfo.StockMinuteDealInfoService;
import com.fox.spider.stock.api.nets.NetsRealtimeMinuteDealInfoApi;
import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.po.nets.NetsRealtimeMinuteDealInfoPo;
import com.fox.spider.stock.entity.po.nets.NetsRealtimeMinuteNodeDataPo;
import com.fox.spider.stock.entity.vo.StockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 股票分钟交易信息服务实现类
 *
 * @author lusongsong
 * @date 2020/12/11 13:50
 */
@Service
public class StockMinuteDealInfoImpl implements StockMinuteDealInfoService {
    /**
     * 股票网易实时分钟数据爬虫接口
     */
    @Autowired
    NetsRealtimeMinuteDealInfoApi netsRealtimeMinuteDealInfoApi;

    /**
     * 从爬虫数据接口中获取实时分钟交易信息
     *
     * @param stockVo
     * @return
     */
    @Override
    public StockRealtimeMinuteDealInfoPo realtimeFromSpiderApi(StockVo stockVo) {
        if (null == stockVo || null == stockVo.getStockMarket() || null == stockVo.getStockCode()) {
            return null;
        }
        StockRealtimeMinuteDealInfoPo stockRealtimeMinuteDealInfoPo = null;
        switch (stockVo.getStockMarket()) {
            case StockConst.SM_SH:
            case StockConst.SM_SZ:
                NetsRealtimeMinuteDealInfoPo netsRealtimeMinuteDealInfoPo =
                        netsRealtimeMinuteDealInfoApi.realtimeMinuteKLine(stockVo);
                if (null != netsRealtimeMinuteDealInfoPo) {
                    stockRealtimeMinuteDealInfoPo = new StockRealtimeMinuteDealInfoPo();
                    stockRealtimeMinuteDealInfoPo.setStockMarket(stockVo.getStockMarket());
                    stockRealtimeMinuteDealInfoPo.setStockCode(stockVo.getStockCode());
                    stockRealtimeMinuteDealInfoPo.setStockName(netsRealtimeMinuteDealInfoPo.getStockName());
                    stockRealtimeMinuteDealInfoPo.setDt(netsRealtimeMinuteDealInfoPo.getDt());
                    stockRealtimeMinuteDealInfoPo.setPreClosePrice(netsRealtimeMinuteDealInfoPo.getPreClosePrice());
                    stockRealtimeMinuteDealInfoPo.setDealNum(netsRealtimeMinuteDealInfoPo.getDealNum());
                    if (null != netsRealtimeMinuteDealInfoPo.getKlineData()
                            && !netsRealtimeMinuteDealInfoPo.getKlineData().isEmpty()) {
                        List<StockRealtimeMinuteNodeDealInfoPo> stockRealtimeMinuteNodeDealInfoPoList = new ArrayList<>(
                                netsRealtimeMinuteDealInfoPo.getKlineData().size()
                        );
                        for (NetsRealtimeMinuteNodeDataPo netsRealtimeMinuteNodeDataPo
                                : netsRealtimeMinuteDealInfoPo.getKlineData()) {
                            StockRealtimeMinuteNodeDealInfoPo stockRealtimeMinuteNodeDealInfoPo =
                                    new StockRealtimeMinuteNodeDealInfoPo();
                            stockRealtimeMinuteNodeDealInfoPo.setTime(netsRealtimeMinuteNodeDataPo.getTime());
                            stockRealtimeMinuteNodeDealInfoPo.setPrice(netsRealtimeMinuteNodeDataPo.getPrice());
                            stockRealtimeMinuteNodeDealInfoPo.setAvgPrice(netsRealtimeMinuteNodeDataPo.getAvgPrice());
                            stockRealtimeMinuteNodeDealInfoPo.setDealNum(netsRealtimeMinuteNodeDataPo.getDealNum());
                            stockRealtimeMinuteNodeDealInfoPoList.add(stockRealtimeMinuteNodeDealInfoPo);
                        }
                        stockRealtimeMinuteDealInfoPo.setKlineData(stockRealtimeMinuteNodeDealInfoPoList);
                    }
                }
                break;
        }
        return stockRealtimeMinuteDealInfoPo;
    }
}
