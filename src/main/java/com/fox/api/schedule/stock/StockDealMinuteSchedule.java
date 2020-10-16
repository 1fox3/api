package com.fox.api.schedule.stock;

import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
import com.fox.api.constant.StockConst;
import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.dao.stock.entity.StockPriceMinuteEntity;
import com.fox.api.dao.stock.mapper.StockPriceMinuteMapper;
import com.fox.api.entity.po.third.stock.StockRealtimeLinePo;
import com.fox.api.entity.po.third.stock.StockRealtimeNodePo;
import com.fox.api.service.third.stock.nets.api.NetsMinuteRealtime;
import com.fox.api.service.third.stock.nets.api.NetsStockBaseApi;
import com.fox.api.util.StockUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * 同步交易分钟级数据
 * @author lusongsong
 * @date 2020/10/15 16:59
 */
@Component
public class StockDealMinuteSchedule extends StockBaseSchedule implements StockScheduleHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    StockPriceMinuteMapper stockPriceMinuteMapper;
    private static final Integer SAVE_DAYS = 5;
    private static final Integer BAK_ONCE_LIMIT = 100000;

    /**
     * 处理单只股票
     *
     * @param stockEntity
     */
    @Override
    public void handle(StockEntity stockEntity) {
        if (null == stockEntity || null == stockEntity.getNetsStockCode()
                || stockEntity.getNetsStockCode().isEmpty()) {
            return;
        }
        try {
            NetsMinuteRealtime netsMinuteRealtime = new NetsMinuteRealtime();
            StockRealtimeLinePo stockRealtimeLinePo = netsMinuteRealtime.getRealtimeData(
                    NetsStockBaseApi.getNetsStockInfoMap(stockEntity)
            );
            if (null == stockRealtimeLinePo) {
                return;
            }
            List<StockRealtimeNodePo> stockRealtimeNodePoList = stockRealtimeLinePo.getLineNode();
            if (null == stockRealtimeNodePoList || stockRealtimeNodePoList.isEmpty()) {
                return;
            }
            List<StockPriceMinuteEntity> stockPriceMinuteEntityList = new LinkedList<>();
            for (StockRealtimeNodePo stockRealtimeNodePo : stockRealtimeNodePoList) {
                if (null == stockRealtimeNodePo || null == stockRealtimeNodePo.getPrice()) {
                    continue;
                }
                StockPriceMinuteEntity stockPriceMinuteEntity = new StockPriceMinuteEntity();
                stockPriceMinuteEntity.setStockId(stockEntity.getId());
                stockPriceMinuteEntity.setDt(stockRealtimeLinePo.getDt());
                stockPriceMinuteEntity.setTime(stockRealtimeNodePo.getTime());
                stockPriceMinuteEntity.setPrice(stockRealtimeNodePo.getPrice());
                stockPriceMinuteEntity.setAvgPrice(stockRealtimeNodePo.getAvgPrice());
                stockPriceMinuteEntity.setDealNum(stockRealtimeNodePo.getDealNum());
                stockPriceMinuteEntityList.add(stockPriceMinuteEntity);
            }
            if (!stockPriceMinuteEntityList.isEmpty()) {
                stockPriceMinuteMapper.batchInsert(stockPriceMinuteEntityList);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 同步交易日当天的分钟级交易信息数据
     */
    @LogShowTimeAnt
    public void syncLastDealDateMinutePriceInfo() {
        try {
            if (StockUtil.todayIsDealDate(StockConst.SM_A)) {
                aStockMarketScan(this);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 备份同步信息
     */
    @LogShowTimeAnt
    public void backupMinutePriceInfo() {
        try {
            List<String> dtList = stockPriceMinuteMapper.dtList();
            if (null == dtList || dtList.size() < SAVE_DAYS) {
                return;
            }
            for (Integer i = SAVE_DAYS - 1; i <= dtList.size(); i++) {
                String clearDt = dtList.get(i);
                Integer dtCount = stockPriceMinuteMapper.dtRowCount(clearDt);
                if (0 == dtCount) {
                    continue;
                }
                for (Integer j = 0; j < dtCount; j += BAK_ONCE_LIMIT) {
                    stockPriceMinuteMapper.bak(clearDt, BAK_ONCE_LIMIT);
                    stockPriceMinuteMapper.delete(clearDt, BAK_ONCE_LIMIT);
                }
            }
            stockPriceMinuteMapper.optimize();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

}
