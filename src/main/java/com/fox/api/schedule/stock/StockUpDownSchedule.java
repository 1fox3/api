package com.fox.api.schedule.stock;

import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.dao.stock.entity.StockLimitUpDownEntity;
import com.fox.api.dao.stock.entity.StockUpDownEntity;
import com.fox.api.dao.stock.mapper.StockLimitUpDownMapper;
import com.fox.api.dao.stock.mapper.StockUpDownMapper;
import com.fox.api.entity.dto.stock.offline.StockDealDayDto;
import com.fox.api.entity.dto.stock.offline.StockDealDayLineDto;
import com.fox.api.service.stock.StockOfflineService;
import com.fox.api.util.DateUtil;
import com.fox.api.util.StockUtil;
import com.fox.spider.stock.constant.StockConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

/**
 * 股票增幅统计
 * @author lusongsong
 */
@Component
public class StockUpDownSchedule extends StockBaseSchedule implements StockScheduleHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private StockUpDownMapper stockUpDownMapper;

    @Autowired
    private StockLimitUpDownMapper stockLimitUpDownMapper;

    @Autowired
    private StockOfflineService stockOfflineService;

    /**
     * 涨跌统计时间粒度
     */
    List<Integer> scopeList = Arrays.asList(10, 30, 50, 100, 200, 300);
    /**
     * 需要的数据起始日期
     */
    String startDate;
    /**
     * 最新交易日
     */
    String lastDealDate;

    /**
     * 删除影子表
     */
    private void dropShadowTable() {
        stockUpDownMapper.dropShadow();
        stockLimitUpDownMapper.dropShadow();
    }

    /**
     * 创建影子表
     */
    private void createShadowTable() {
        stockUpDownMapper.createShadow();
        stockLimitUpDownMapper.createShadow();
    }

    /**
     * 名称转换
     */
    private void shadowTableConvert() {
        stockUpDownMapper.shadowConvert();
        stockLimitUpDownMapper.shadowConvert();
    }

    /**
     * 优化表
     */
    private void optimizeTable() {
        stockUpDownMapper.optimize();
        stockLimitUpDownMapper.optimize();
    }

    /**
     * 股票增幅统计
     * @param stockEntity
     * @param stockDealDayDtoList
     */
    private void upDown(StockEntity stockEntity, List<StockDealDayDto> stockDealDayDtoList) {
        if (null == stockEntity || null == stockDealDayDtoList || 0 >= stockDealDayDtoList.size()) {
            return;
        }

        int len = stockDealDayDtoList.size();
        //需要的最大天数
        Integer limitLen = scopeList.get(scopeList.size() - 1);
        StockUpDownEntity stockUpDownEntity = new StockUpDownEntity();
        stockUpDownEntity.setStockId(stockEntity.getId());

        BigDecimal currentPrice , highestPrice, lowestPrice;
        currentPrice = highestPrice = lowestPrice = BigDecimal.ZERO;

        Integer pos = len;
        StockDealDayDto stockDealDayDto = new StockDealDayDto();
        for (int j = 0; j <= limitLen; j++) {
            while (pos > 0) {
                pos--;
                stockDealDayDto = stockDealDayDtoList.get(pos);
                if (null == stockDealDayDto || null == stockDealDayDto.getClosePrice()
                        || 0 <= BigDecimal.ZERO.compareTo(stockDealDayDto.getClosePrice())) {
                    continue;
                }
                break;
            }
            currentPrice = 0 == currentPrice.compareTo(BigDecimal.ZERO) ? stockDealDayDto.getClosePrice() : currentPrice;
            highestPrice = 0 > highestPrice.compareTo(stockDealDayDto.getHighestPrice())
                    ? stockDealDayDto.getHighestPrice() : highestPrice;
            lowestPrice = 0 == lowestPrice.compareTo(BigDecimal.ZERO)
                    || (0 < lowestPrice.compareTo(stockDealDayDto.getLowestPrice())
                    && 0 > BigDecimal.ZERO.compareTo(stockDealDayDto.getLowestPrice()))
                    ? stockDealDayDto.getLowestPrice() : lowestPrice;
            if (scopeList.contains(j) && 0 < currentPrice.compareTo(BigDecimal.ZERO)
                    && 0 < highestPrice.compareTo(BigDecimal.ZERO)
                    && 0 < lowestPrice.compareTo(BigDecimal.ZERO)
            ) {
                BigDecimal up = currentPrice.subtract(lowestPrice).divide(lowestPrice, 4, RoundingMode.HALF_UP);
                BigDecimal down = highestPrice.subtract(currentPrice).divide(highestPrice, 4, RoundingMode.HALF_UP);
                if (10 == j) {
                    stockUpDownEntity.setD10Up(up);
                    stockUpDownEntity.setD10Down(down);
                }
                if (30 == j) {
                    stockUpDownEntity.setD30Up(up);
                    stockUpDownEntity.setD30Down(down);
                }
                if (50 == j) {
                    stockUpDownEntity.setD50Up(up);
                    stockUpDownEntity.setD50Down(down);
                }
                if (100 == j) {
                    stockUpDownEntity.setD100Up(up);
                    stockUpDownEntity.setD100Down(down);
                }
                if (200 == j) {
                    stockUpDownEntity.setD200Up(up);
                    stockUpDownEntity.setD200Down(down);
                }
                if (300 == j) {
                    stockUpDownEntity.setD300Up(up);
                    stockUpDownEntity.setD300Down(down);
                }
            }
        }
        stockUpDownMapper.insert(stockUpDownEntity);
    }

    /**
     * 股票连续涨跌停统计
     * @param stockEntity
     * @param stockDealDayDtoList
     */
    private void limitUpDown(StockEntity stockEntity, List<StockDealDayDto> stockDealDayDtoList) {
        if (null == stockEntity || null == stockDealDayDtoList || 0 >= stockDealDayDtoList.size()) {
            return;
        }
        BigDecimal limitRate = StockUtil.limitRate(stockEntity);
        //未设增幅限制的忽略
        if (null == limitRate || 0 <= BigDecimal.ZERO.compareTo(limitRate)) {
            return;
        }
        int len = stockDealDayDtoList.size();
        StockDealDayDto todayDealDto = null;
        StockDealDayDto yesterdayDealDto = null;
        BigDecimal todayPrice = null;
        BigDecimal yesterdayPrice = null;
        StockLimitUpDownEntity stockLimitUpDownEntity = new StockLimitUpDownEntity();
        stockLimitUpDownEntity.setStockId(stockEntity.getId());
        for (int i = len - 1; i >= 0; i--) {
            StockDealDayDto currentDealDto = stockDealDayDtoList.get(i);
            if (null == currentDealDto || 0 <= BigDecimal.ZERO.compareTo(currentDealDto.getClosePrice())) {
                continue;
            }
            todayDealDto = yesterdayDealDto;
            yesterdayDealDto = currentDealDto;
            if (null == yesterdayDealDto || null == todayDealDto) {
                continue;
            }
            todayPrice = todayDealDto.getClosePrice().setScale(2, RoundingMode.HALF_UP);
            yesterdayPrice = yesterdayDealDto.getClosePrice().setScale(2, RoundingMode.HALF_UP);

            BigDecimal uptickPrice = todayPrice.subtract(yesterdayPrice).abs();
            BigDecimal limitRatePrice = yesterdayPrice.multiply(limitRate).abs().setScale(2, RoundingMode.HALF_UP);

            //type值说明 0-正常，1-涨停，2-跌停
            Integer type = 0;
            if (uptickPrice.equals(limitRatePrice)) {
                type = 0 < todayPrice.compareTo(yesterdayPrice) ? 1 : 2;
            }
            //不涨，涨跌发生转变则停止
            if (type.equals(0)
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
                stockLimitUpDownEntity.setCurrentDate(todayDealDto.getDt());
            }

            stockLimitUpDownEntity.setStartPrice(yesterdayPrice);
            stockLimitUpDownEntity.setStartDate(yesterdayDealDto.getDt());
        }
        if (null != stockLimitUpDownEntity.getType()) {
            stockLimitUpDownMapper.insert(stockLimitUpDownEntity);
        }
    }

    /**
     * 执行时间请设置在股市结束之后，因为接口会反回当天数据，影响涨跌停的判断
     */
    @LogShowTimeAnt
    public void stockUpDown() {
        if (!StockUtil.todayIsDealDate(StockConst.SM_A)) {
            return;
        }

        //获取近2年的交易记录
        startDate = DateUtil.getRelateDate(-2, 0, 0, DateUtil.DATE_FORMAT_1);
        //最新交易日
        lastDealDate = StockUtil.lastDealDate(StockConst.SM_A);

        //将涨幅统计的影子表删除
        this.dropShadowTable();
        //创建涨幅统计的影子表
        this.createShadowTable();

        //遍历A股
        aStockMarketScan(this);

        //涨幅统计的影子表与正式表进行名称互换
        this.shadowTableConvert();
        //将涨幅统计的影子表删除(前正式表)
        this.dropShadowTable();
        //优化正式表空间
        this.optimizeTable();
    }

    /**
     * 处理单只股票
     *
     * @param stockEntity
     */
    @Override
    public void handle(StockEntity stockEntity) {
        try{
            if (null != stockEntity && null != stockEntity.getId()) {
                startDate = null == startDate ?
                        DateUtil.getRelateDate(-2, 0, 0, DateUtil.DATE_FORMAT_1) : startDate;
                StockDealDayLineDto stockDealDayLineDto = stockOfflineService.line(stockEntity.getId(), startDate);
                List<StockDealDayDto> stockDealDayDtoList = stockDealDayLineDto.getLineNode();
                if (null == stockDealDayDtoList || stockDealDayDtoList.isEmpty()) {
                    return;
                }
                //有最新交易数据才统计
                StockDealDayDto lastStockDealDayDto = stockDealDayDtoList.get(stockDealDayDtoList.size() - 1);
                if (null == lastStockDealDayDto || null == lastStockDealDayDto.getDt()
                        || !lastStockDealDayDto.getDt().equals(lastDealDate)) {
                    return;
                }
                upDown(stockEntity, stockDealDayDtoList);
                limitUpDown(stockEntity, stockDealDayDtoList);
            }
        } catch (Exception e){
            logger.error(stockEntity.toString());
            logger.error(e.getMessage());
        }
    }
}
