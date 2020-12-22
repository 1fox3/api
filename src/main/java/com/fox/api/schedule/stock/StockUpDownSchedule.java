package com.fox.api.schedule.stock;

import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
import com.fox.api.dao.stock.entity.StockDealDayEntity;
import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.dao.stock.entity.StockLimitUpDownEntity;
import com.fox.api.dao.stock.entity.StockUpDownEntity;
import com.fox.api.dao.stock.mapper.StockDealDayMapper;
import com.fox.api.dao.stock.mapper.StockLimitUpDownMapper;
import com.fox.api.dao.stock.mapper.StockUpDownMapper;
import com.fox.api.util.DateUtil;
import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.vo.StockVo;
import com.fox.spider.stock.service.StockToolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 股票增幅统计
 *
 * @author lusongsong
 * @date 2020/12/22 17:55
 */
@Component
public class StockUpDownSchedule extends StockBaseSchedule implements StockScheduleHandler {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 股票涨跌幅统计数据库操作类
     */
    @Autowired
    private StockUpDownMapper stockUpDownMapper;
    /**
     * 股票涨跌停统计数据库操作类
     */
    @Autowired
    private StockLimitUpDownMapper stockLimitUpDownMapper;
    /**
     * 按天交易数据库操作类
     */
    @Autowired
    private StockDealDayMapper stockDealDayMapper;
    /**
     * 股票工具服务
     */
    @Autowired
    StockToolService stockToolService;
    /**
     * 涨跌统计时间粒度
     */
    List<Integer> scopeList = Arrays.asList(10, 30, 50, 100, 200, 300);
    /**
     * 获取数据起始日期
     */
    private String startDate;
    /**
     * 获取数据截止日期
     */
    private String endDate;

    /**
     * 删除影子表
     */
    private void dropShadowTable() {
        try {
            stockUpDownMapper.dropShadow();
        } catch (Exception e) {
            logger.error("dropStockUpDownShadowTable", e);
        }
        try {
            stockLimitUpDownMapper.dropShadow();
        } catch (Exception e) {
            logger.error("dropStockLimitUpDownShadowTable", e);
        }
    }

    /**
     * 创建影子表
     */
    private void createShadowTable() {
        try {
            stockUpDownMapper.createShadow();
        } catch (Exception e) {
            logger.error("createStockUpDownShadowTable", e);
        }
        try {
            stockLimitUpDownMapper.createShadow();
        } catch (Exception e) {
            logger.error("createStockLimitUpDownShadowTable", e);
        }
    }

    /**
     * 名称转换
     */
    private void shadowTableConvert() {
        try {
            stockUpDownMapper.shadowConvert();
        } catch (Exception e) {
            logger.error("convertStockUpDownShadowTable", e);
        }
        try {
            stockLimitUpDownMapper.shadowConvert();
        } catch (Exception e) {
            logger.error("convertStockLimitUpDownShadowTable", e);
        }
    }

    /**
     * 优化表
     */
    private void optimizeTable() {
        try {
            stockUpDownMapper.optimize();
        } catch (Exception e) {
            logger.error("optimizeStockUpDownShadowTable", e);
        }
        try {
            stockLimitUpDownMapper.optimize();
        } catch (Exception e) {
            logger.error("optimizeStockLimitUpDownShadowTable", e);
        }
    }

    /**
     * 股票增幅统计
     *
     * @param stockEntity
     * @param stockDealDayEntityList
     */
    private void upDown(StockEntity stockEntity, List<StockDealDayEntity> stockDealDayEntityList) {
        if (null == stockEntity || null == stockDealDayEntityList || stockDealDayEntityList.isEmpty()) {
            return;
        }

        BigDecimal currentPrice, highestPrice, lowestPrice;
        currentPrice = highestPrice = lowestPrice = BigDecimal.ZERO;

        StockDealDayEntity stockDealDayEntity = null;
        List<StockUpDownEntity> stockUpDownEntityList = new ArrayList<>();
        for (int i = 0; i < stockDealDayEntityList.size(); i++) {
            stockDealDayEntity = stockDealDayEntityList.get(i);
            if (null == stockDealDayEntity || null == stockDealDayEntity.getClosePrice()
                    || 0 <= BigDecimal.ZERO.compareTo(stockDealDayEntity.getClosePrice())) {
                continue;
            }

            currentPrice = 0 == currentPrice.compareTo(BigDecimal.ZERO) ?
                    stockDealDayEntity.getClosePrice() : currentPrice;
            highestPrice = 0 > highestPrice.compareTo(stockDealDayEntity.getHighestPrice())
                    ? stockDealDayEntity.getHighestPrice() : highestPrice;
            lowestPrice = 0 == lowestPrice.compareTo(BigDecimal.ZERO)
                    || (0 < lowestPrice.compareTo(stockDealDayEntity.getLowestPrice())
                    && 0 > BigDecimal.ZERO.compareTo(stockDealDayEntity.getLowestPrice()))
                    ? stockDealDayEntity.getLowestPrice() : lowestPrice;
            if (scopeList.contains(i) && 0 < currentPrice.compareTo(BigDecimal.ZERO)
                    && 0 < highestPrice.compareTo(BigDecimal.ZERO)
                    && 0 < lowestPrice.compareTo(BigDecimal.ZERO)
            ) {
                BigDecimal up = currentPrice.subtract(lowestPrice).divide(lowestPrice, 4, RoundingMode.HALF_UP);
                BigDecimal down = highestPrice.subtract(currentPrice).divide(highestPrice, 4, RoundingMode.HALF_UP);

                StockUpDownEntity stockUpDownEntity = new StockUpDownEntity();
                stockUpDownEntity.setStockId(stockEntity.getId());
                stockUpDownEntity.setDayNum(i);
                stockUpDownEntity.setUpRate(up);
                stockUpDownEntity.setDownRate(down);
                stockUpDownEntityList.add(stockUpDownEntity);
            }
        }
        if (null != stockDealDayEntityList && !stockUpDownEntityList.isEmpty()) {
            try {
                stockUpDownMapper.batchInsert(stockUpDownEntityList);
            } catch (Exception e) {
                logger.error("upDown", e);
            }
        }
    }

    /**
     * 股票连续涨跌停统计
     *
     * @param stockEntity
     * @param stockDealDayEntityList
     */
    private void limitUpDown(StockEntity stockEntity, List<StockDealDayEntity> stockDealDayEntityList) {
        if (null == stockEntity || null == stockDealDayEntityList || stockDealDayEntityList.isEmpty()) {
            return;
        }
        BigDecimal limitRate = stockToolService.limitRate(
                new StockVo(stockEntity.getStockCode(), stockEntity.getStockMarket()), stockEntity.getStockName()
        );
        //未设增幅限制的忽略
        if (null == limitRate || 0 <= BigDecimal.ZERO.compareTo(limitRate)) {
            return;
        }
        BigDecimal todayPrice = null;
        BigDecimal yesterdayPrice = null;
        StockLimitUpDownEntity stockLimitUpDownEntity = new StockLimitUpDownEntity();
        stockLimitUpDownEntity.setStockId(stockEntity.getId());
        StockDealDayEntity stockDealDayEntity = null;
        for (int i = 0; i < stockDealDayEntityList.size(); i++) {
            stockDealDayEntity = stockDealDayEntityList.get(i);
            if (null == stockDealDayEntity || null == stockDealDayEntity.getClosePrice()
                    || 0 <= BigDecimal.ZERO.compareTo(stockDealDayEntity.getClosePrice())) {
                continue;
            }
            todayPrice = stockDealDayEntity.getClosePrice();
            yesterdayPrice = stockDealDayEntity.getPreClosePrice();

            BigDecimal uptickPrice = todayPrice.subtract(yesterdayPrice).abs();
            BigDecimal limitRatePrice = yesterdayPrice.multiply(limitRate).abs().setScale(2, RoundingMode.HALF_UP);

            //type值说明 0-正常，1-涨停，2-跌停
            Integer type = StockConst.UPTICK_TYPE_FLAT;
            if (uptickPrice.equals(limitRatePrice)) {
                type = 0 < todayPrice.compareTo(yesterdayPrice) ?
                        StockConst.UPTICK_TYPE_TOP_UP : StockConst.UPTICK_TYPE_TOP_DOWN;
            }
            //不涨，涨跌发生转变则停止
            if (type.equals(StockConst.UPTICK_TYPE_FLAT)
                    || (null != stockLimitUpDownEntity.getType() && !type.equals(stockLimitUpDownEntity.getType()))
            ) {
                break;
            }

            stockLimitUpDownEntity.setType(type);
            if (null == stockLimitUpDownEntity.getNum()) {
                stockLimitUpDownEntity.setNum(1);
            } else {
                stockLimitUpDownEntity.setNum(stockLimitUpDownEntity.getNum() + 1);
            }

            if (null == stockLimitUpDownEntity.getCurrentPrice()) {
                stockLimitUpDownEntity.setCurrentPrice(todayPrice);
                stockLimitUpDownEntity.setCurrentDate(stockDealDayEntity.getDt());
            }

            stockLimitUpDownEntity.setStartPrice(yesterdayPrice);
            stockLimitUpDownEntity.setStartDate(stockDealDayEntity.getDt());
        }
        if (null != stockLimitUpDownEntity.getType()) {
            try {
                stockLimitUpDownMapper.insert(stockLimitUpDownEntity);
            } catch (Exception e) {
                logger.error("limitUpDown", e);
            }
        }
    }

    /**
     * 执行时间请设置在股市结束之后，因为接口会反回当天数据，影响涨跌停的判断
     */
    @LogShowTimeAnt
    public void stockUpDown() {
        startDate = DateUtil.getRelateDate(-2, 0, 0, DateUtil.DATE_FORMAT_1);
        endDate = DateUtil.getCurrentDate();

        //将涨幅统计的影子表删除
        dropShadowTable();
        //创建涨幅统计的影子表
        createShadowTable();
        for (Integer sm : StockConst.SM_A_LIST) {
            stockMarketScan(sm, this);
        }
        //涨幅统计的影子表与正式表进行名称互换
        shadowTableConvert();
        //将涨幅统计的影子表删除(前正式表)
        dropShadowTable();
        //优化正式表空间
        optimizeTable();
    }

    /**
     * 处理单只股票
     *
     * @param stockEntity
     */
    @Override
    public void handle(StockEntity stockEntity) {
        try {
            if (null != stockEntity && null != stockEntity.getId() && 1 != stockEntity.getStockStatus()) {
                List<StockDealDayEntity> stockDealDayEntityList = stockDealDayMapper.getByDate(
                        stockEntity.getId(), StockConst.SFQ_AFTER, startDate, endDate
                );
                if (null == stockDealDayEntityList || stockDealDayEntityList.isEmpty()) {
                    return;
                }
                //按日期降序排列
                Collections.reverse(stockDealDayEntityList);
                upDown(stockEntity, stockDealDayEntityList);
                limitUpDown(stockEntity, stockDealDayEntityList);
            }
        } catch (Exception e) {
            logger.error(stockEntity.toString());
            logger.error("handle", e);
        }
    }
}
