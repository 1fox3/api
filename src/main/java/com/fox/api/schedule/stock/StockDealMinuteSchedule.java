package com.fox.api.schedule.stock;

import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
import com.fox.api.constant.stock.StockTableDtConst;
import com.fox.api.dao.stock.entity.StockDealMinuteEntity;
import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.dao.stock.entity.StockTableDtEntity;
import com.fox.api.dao.stock.mapper.StockDealMinuteMapper;
import com.fox.api.entity.po.stock.api.StockRealtimeMinuteKLinePo;
import com.fox.api.entity.po.stock.api.StockRealtimeMinuteNodeDataPo;
import com.fox.api.entity.po.stock.dealinfo.StockRealtimeMinuteDealInfoPo;
import com.fox.api.entity.po.stock.dealinfo.StockRealtimeMinuteNodeDealInfoPo;
import com.fox.api.schedule.stock.handler.StockScheduleHandler;
import com.fox.api.service.stock.StockTableDtService;
import com.fox.api.service.stock.api.request.StockRealtimeMinuteKLineApiService;
import com.fox.api.service.stock.dealinfo.StockMinuteDealInfoService;
import com.fox.api.util.StockUtil;
import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.vo.StockVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 同步交易分钟级数据
 *
 * @author lusongsong
 * @date 2020/10/15 16:59
 */
@Component
public class StockDealMinuteSchedule extends StockBaseSchedule implements StockScheduleHandler {
    /**
     * 日志工具
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 股票分钟交易数据服务
     */
    @Autowired
    StockRealtimeMinuteKLineApiService stockRealtimeMinuteKLineApiService;
    /**
     * 分钟交易数据表
     */
    @Autowired
    StockDealMinuteMapper stockDealMinuteMapper;
    /**
     * 数据表天信息表
     */
    @Autowired
    StockTableDtService stockTableDtService;
    /**
     * 当前交易日
     */
    private String lastDealDate = "";

    /**
     * 处理单只股票
     *
     * @param stockEntity
     */
    @Override
    public void handle(StockEntity stockEntity) {
        if (null == stockEntity || null == stockEntity.getStockCode() || stockEntity.getStockCode().isEmpty()
                || null == stockEntity.getStockStatus() || 1 == stockEntity.getStockStatus()) {
            return;
        }
        try {
            StockRealtimeMinuteKLinePo stockRealtimeMinuteKLinePo =
                    stockRealtimeMinuteKLineApiService.realtimeMinuteKLine(
                            new StockVo(stockEntity.getStockCode(), stockEntity.getStockMarket())
                    );
            if (null == stockRealtimeMinuteKLinePo || !lastDealDate.equals(stockRealtimeMinuteKLinePo.getDt())) {
                return;
            }
            List<StockRealtimeMinuteNodeDataPo> stockRealtimeMinuteNodeDataPoList =
                    stockRealtimeMinuteKLinePo.getKlineData();
            if (null == stockRealtimeMinuteNodeDataPoList || stockRealtimeMinuteNodeDataPoList.isEmpty()) {
                return;
            }
            List<StockDealMinuteEntity> stockDealMinuteEntityList =
                    new ArrayList<>(stockRealtimeMinuteNodeDataPoList.size());
            for (StockRealtimeMinuteNodeDataPo stockRealtimeMinuteNodeDataPo
                    : stockRealtimeMinuteNodeDataPoList) {
                if (null == stockRealtimeMinuteNodeDataPo || null == stockRealtimeMinuteNodeDataPo.getPrice()) {
                    continue;
                }
                StockDealMinuteEntity stockDealMinuteEntity = new StockDealMinuteEntity();
                stockDealMinuteEntity.setStockId(stockEntity.getId());
                stockDealMinuteEntity.setDt(stockRealtimeMinuteKLinePo.getDt());
                stockDealMinuteEntity.setTime(stockRealtimeMinuteNodeDataPo.getTime());
                stockDealMinuteEntity.setPrice(stockRealtimeMinuteNodeDataPo.getPrice());
                stockDealMinuteEntity.setDealNum(stockRealtimeMinuteNodeDataPo.getDealNum());
                stockDealMinuteEntityList.add(stockDealMinuteEntity);
            }
            if (!stockDealMinuteEntityList.isEmpty()) {
                stockDealMinuteMapper.batchInsert(stockDealMinuteEntityList);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 记录日期
     */
    private void logDt() {
        StockTableDtEntity stockTableDtEntity = new StockTableDtEntity();
        stockTableDtEntity.setTable(StockTableDtConst.TABLE_KEY_DEAL_MINUTE);
        stockTableDtEntity.setDt(lastDealDate);
        stockTableDtEntity.setType(StockTableDtConst.TYPE_DEFAULT);
        stockTableDtService.insert(stockTableDtEntity);
    }

    /**
     * 同步数据
     *
     * @param stockMarket
     */
    private void syncStockMarketData(Integer stockMarket) {
        if (null == stockMarket || !StockConst.SM_ALL.contains(stockMarket)) {
            return;
        }
        //同步TOP指数
        stockMarketTopIndexScan(stockMarket, this);
        //同步股票
        stockMarketScan(stockMarket, this);
    }

    /**
     * 同步交易日当天的分钟级交易信息数据
     */
    @LogShowTimeAnt
    public void syncLastDealDateMinuteDealInfo(Integer stockMarket) {
        if (StockConst.SM_ALL.contains(stockMarket) && StockUtil.todayIsDealDate(stockMarket)) {
            lastDealDate = StockUtil.lastDealDate(stockMarket);
            logDt();
            if (StockConst.SM_A == stockMarket) {
                for (Integer sm : StockConst.SM_A_LIST) {
                    syncStockMarketData(sm);
                }
            } else {
                syncStockMarketData(stockMarket);
            }
            stockDealMinuteMapper.optimize();
        }
    }
}
