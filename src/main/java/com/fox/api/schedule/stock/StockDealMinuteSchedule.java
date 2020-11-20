package com.fox.api.schedule.stock;

import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
import com.fox.api.constant.stock.StockTableDtConst;
import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.dao.stock.entity.StockPriceMinuteEntity;
import com.fox.api.dao.stock.entity.StockTableDtEntity;
import com.fox.api.dao.stock.mapper.StockPriceMinuteMapper;
import com.fox.api.entity.po.third.stock.StockRealtimeLinePo;
import com.fox.api.entity.po.third.stock.StockRealtimeNodePo;
import com.fox.api.service.stock.StockTableDtService;
import com.fox.api.service.third.stock.nets.api.NetsMinuteRealtime;
import com.fox.api.service.third.stock.nets.api.NetsStockBaseApi;
import com.fox.api.util.StockUtil;
import com.fox.spider.stock.constant.StockConst;
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
    @Autowired
    StockTableDtService stockTableDtService;

    private static final Integer SAVE_DAYS = 5;
    private static final Integer BAK_ONCE_LIMIT = 100000;

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
            NetsMinuteRealtime netsMinuteRealtime = new NetsMinuteRealtime();
            StockRealtimeLinePo stockRealtimeLinePo = netsMinuteRealtime.getRealtimeData(
                    NetsStockBaseApi.getNetsStockInfoMap(stockEntity)
            );
            if (null == stockRealtimeLinePo || !lastDealDate.equals(stockRealtimeLinePo.getDt())) {
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
     * 记录日期
     */
    private void logDt() {
        StockTableDtEntity stockTableDtEntity = new StockTableDtEntity();
        stockTableDtEntity.setTable(StockTableDtConst.TABLE_PRICE_MINUTE);
        stockTableDtEntity.setDt(lastDealDate);
        stockTableDtEntity.setType(StockTableDtConst.TYPE_DEFAULT);
        stockTableDtService.insert(stockTableDtEntity);
    }

    /**
     * 同步交易日当天的分钟级交易信息数据
     */
    @LogShowTimeAnt
    public void syncLastDealDateMinutePriceInfo() {
        try {
            if (StockUtil.todayIsDealDate(StockConst.SM_A)) {
                lastDealDate = StockUtil.lastDealDate(StockConst.SM_A);
                logDt();
                //同步TOP指数
                aStockMarketTopIndexScan(this);
                //同步股票
                aStockMarketScan(this);
                //数据备份
                backupMinutePriceInfo();
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
            //创建备份表
            stockPriceMinuteMapper.createBak();

            //判断日期
            StockTableDtEntity stockTableDtEntity = new StockTableDtEntity();
            stockTableDtEntity.setTable(StockTableDtConst.TABLE_PRICE_MINUTE);
            stockTableDtEntity.setType(StockTableDtConst.TYPE_DEFAULT);
            List<String> dtList = stockTableDtService.getByType(stockTableDtEntity);
            if (null == dtList || dtList.isEmpty() || dtList.size() < SAVE_DAYS) {
                return;
            }
            Integer bakDtNum = dtList.size() - SAVE_DAYS;
            for (int i = 0; i < bakDtNum; i++) {
                while (true) {
                    stockPriceMinuteMapper.bak(dtList.get(i), BAK_ONCE_LIMIT);
                    Integer bakCount = stockPriceMinuteMapper.delete(dtList.get(i), BAK_ONCE_LIMIT);
                    if (!BAK_ONCE_LIMIT.equals(bakCount)) {
                        break;
                    }
                    stockTableDtEntity.setDt(dtList.get(i));
                    stockTableDtService.setBak(stockTableDtEntity);
                }
            }

            stockPriceMinuteMapper.optimize();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
