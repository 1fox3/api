package com.fox.api.schedule.stock;

import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
import com.fox.api.constant.StockConst;
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
public class StockUpDownSchedule extends StockBaseSchedule {
    @Autowired
    private StockUpDownMapper stockUpDownMapper;

    @Autowired
    private StockLimitUpDownMapper stockLimitUpDownMapper;

    @Autowired
    private StockOfflineService stockOfflineService;

    List<Integer> scopeList = Arrays.asList(10, 30, 50, 100, 200, 300);

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
        int limitLen = scopeList.get(scopeList.size() - 1);

        StockUpDownEntity stockUpDownEntity = new StockUpDownEntity();
        stockUpDownEntity.setStockId(stockEntity.getId());

        BigDecimal currentPrice , highestPrice, lowestPrice;
        currentPrice = highestPrice = lowestPrice = BigDecimal.ZERO;

        for (int j = 0; j<= limitLen; j++) {
            int pos = len - j - 1;
            if (pos >= 0) {
                StockDealDayDto stockDealDayDto = stockDealDayDtoList.get(pos);
                currentPrice = 0 == currentPrice.compareTo(BigDecimal.ZERO) ? stockDealDayDto.getClosePrice() : currentPrice;

                highestPrice = 1 == stockDealDayDto.getHighestPrice().compareTo(highestPrice) ?
                        stockDealDayDto.getHighestPrice() : highestPrice;
                lowestPrice = 0 == lowestPrice.compareTo(BigDecimal.ZERO)
                        || -1 == stockDealDayDto.getLowestPrice().compareTo(lowestPrice)
                        ? stockDealDayDto.getLowestPrice() : lowestPrice;
            }

            if (scopeList.contains(j) && 1 == currentPrice.compareTo(BigDecimal.ZERO)
                    && 1 == highestPrice.compareTo(BigDecimal.ZERO)
                    && 1 == lowestPrice.compareTo(BigDecimal.ZERO)
            ) {
                BigDecimal up = currentPrice.subtract(lowestPrice).divide(lowestPrice, 2, RoundingMode.HALF_UP);
                BigDecimal down = highestPrice.subtract(currentPrice).divide(highestPrice, 2, RoundingMode.HALF_UP);
                up.setScale(4, RoundingMode.HALF_UP);
                down.setScale(5, RoundingMode.HALF_UP);
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
    private void limitUpDown(StockEntity stockEntity, List<StockDealDayDto> stockDealDayDtoList, BigDecimal limitRate) {
        if (null == stockEntity || null == stockDealDayDtoList || 0 >= stockDealDayDtoList.size()) {
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
            todayDealDto = yesterdayDealDto;
            yesterdayDealDto = currentDealDto;
            if (null == yesterdayDealDto || null == todayDealDto) {
                continue;
            }
            todayPrice = todayDealDto.getClosePrice();
            yesterdayPrice = yesterdayDealDto.getClosePrice();

            if (todayPrice.equals(0) || yesterdayPrice.equals(0)) {
                break;
            }

            //type值说明 0-正常，1-涨停，2-跌停
            Integer type = 0;
            if (-1 != todayPrice.subtract(yesterdayPrice).divide(yesterdayPrice, 4, RoundingMode.HALF_UP).abs().compareTo(limitRate)) {
                type = 1 == todayPrice.compareTo(yesterdayPrice) ? 1 : 2;
            }
            if (type.equals(0) ||
                    (null != stockLimitUpDownEntity.getType() && stockLimitUpDownEntity.getType().equals(type))) {
                break;
            }

            stockLimitUpDownEntity.setType(type);
            if (null == stockLimitUpDownEntity.getNum()) {
                stockLimitUpDownEntity.setNum(1);
            } else {
                stockLimitUpDownEntity.setNum(stockLimitUpDownEntity.getNum() + 1);
            }

            if (null == stockLimitUpDownEntity.getCurrentPrice()) {
                stockLimitUpDownEntity.setCurrentPrice(Float.valueOf(String.valueOf(todayPrice)));
                stockLimitUpDownEntity.setCurrentDate(todayDealDto.getDt());
            }

            stockLimitUpDownEntity.setStartPrice(Float.valueOf(String.valueOf(yesterdayPrice)));
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
        //将涨幅的统计表进行是删除
        this.dropShadowTable();
        this.createShadowTable();

        Integer onceLimit = 200;
        Long stockListSize = this.stockRedisUtil.lSize(this.redisStockList);

        for (Long i = Long.valueOf(0); i < stockListSize; i += onceLimit) {
            List<Object> stockEntityList = this.stockRedisUtil.lRange(this.redisStockList, i, i + onceLimit - 1);
            if (null == stockEntityList || 0 >= stockEntityList.size()) {
                continue;
            }

            for (Object stockEntity : stockEntityList) {
                Integer stockId = ((StockEntity)stockEntity).getId();
                try{
                    if (stockId > 0) {
                        StockDealDayLineDto stockDealDayLineDto = stockOfflineService.line(stockId, DateUtil.getRelateDate(-2, 0, 0, DateUtil.DATE_FORMAT_1));
                        List<StockDealDayDto> stockDealDayDtoList = stockDealDayLineDto.getLineNode();
                        if (null == stockDealDayDtoList) {
                            continue;
                        }
                        int len = stockDealDayDtoList.size();
                        if (0 >= len) {
                            continue;
                        }
                        this.upDown((StockEntity)stockEntity, stockDealDayDtoList);
                        if (stockDealDayLineDto.getStockName().contains("ST")) {
                            this.limitUpDown((StockEntity)stockEntity, stockDealDayDtoList, new BigDecimal(0.05));
                        } else {
                            this.limitUpDown((StockEntity)stockEntity, stockDealDayDtoList, new BigDecimal(0.1));
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        this.shadowTableConvert();
        this.dropShadowTable();
        this.optimizeTable();
    }
}
