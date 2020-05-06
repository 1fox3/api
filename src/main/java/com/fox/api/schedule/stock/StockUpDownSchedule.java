package com.fox.api.schedule.stock;

import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.dao.stock.entity.StockLimitUpDownEntity;
import com.fox.api.dao.stock.entity.StockUpDownEntity;
import com.fox.api.dao.stock.mapper.StockLimitUpDownMapper;
import com.fox.api.dao.stock.mapper.StockMapper;
import com.fox.api.dao.stock.mapper.StockUpDownMapper;
import com.fox.api.entity.dto.stock.offline.StockDealDayDto;
import com.fox.api.entity.dto.stock.offline.StockDealDayLineDto;
import com.fox.api.entity.po.third.stock.StockRealtimePo;
import com.fox.api.service.stock.StockOfflineService;
import com.fox.api.service.third.stock.sina.api.SinaRealtime;
import com.fox.api.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

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

        Double currentPrice , highestPrice, lowestPrice;
        currentPrice = highestPrice = lowestPrice = 0.0;

        for (int j = 0; j<= limitLen; j++) {
            int pos = len - j - 1;
            if (pos >= 0) {
                StockDealDayDto stockDealDayDto = stockDealDayDtoList.get(pos);
                currentPrice = 0.0 == currentPrice ? stockDealDayDto.getClosePrice() : currentPrice;

                highestPrice = stockDealDayDto.getHighestPrice() > highestPrice ?
                        stockDealDayDto.getHighestPrice() : highestPrice;
                lowestPrice = 0.0 == lowestPrice || stockDealDayDto.getLowestPrice() < lowestPrice ?
                        stockDealDayDto.getLowestPrice() : lowestPrice;
            }

            if (scopeList.contains(j) && currentPrice > 0 && lowestPrice > 0 && lowestPrice > 0) {
                float up = (float)((currentPrice - lowestPrice) / lowestPrice);
                float down = (float)((highestPrice - currentPrice) / highestPrice);
                //保留4位小数
                up = (float)Math.round(up * 10000) / 10000;
                down = (float)Math.round(down * 10000) / 10000;
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
    private void limitUpDown(StockEntity stockEntity, List<StockDealDayDto> stockDealDayDtoList, double limitRate) {
        if (null == stockEntity || null == stockDealDayDtoList || 0 >= stockDealDayDtoList.size()) {
            return;
        }
        int len = stockDealDayDtoList.size();
        StockDealDayDto todayDealDto = null;
        StockDealDayDto yesterdayDealDto = null;
        Double todayPrice = null;
        Double yesterdayPrice = null;
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
            if ((float)Math.abs((todayPrice - yesterdayPrice) * 100) / 100 == (float)Math.round(yesterdayPrice * limitRate * 100) / 100) {
                type = todayPrice > yesterdayPrice ? 1 : 2;
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
    //@Scheduled(cron="0 5 15 * * 1-5")
    public void stockUpDown() {
        if (!this.todayIsDealDate()) {
            return;
        }

        //将涨幅的统计表进行是删除
        stockUpDownMapper.truncate();
        stockLimitUpDownMapper.truncate();

        Integer onceLimit = 200;
        Long stockListSize = this.stockRedisUtil.lSize(this.redisStockList);

        for (Long i = Long.valueOf(0); i < stockListSize; i += onceLimit) {
            List<Object> stockEntityList = this.stockRedisUtil.lRange(this.redisStockList, i, i + onceLimit - 1);
            if (null == stockEntityList || 0 >= stockEntityList.size()) {
                continue;
            }

            for (Object stockEntity : stockEntityList) {
                Integer stockId = ((StockEntity)stockEntity).getId();
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
                        this.limitUpDown((StockEntity)stockEntity, stockDealDayDtoList, 0.05);
                    } else {
                        this.limitUpDown((StockEntity)stockEntity, stockDealDayDtoList, 0.1);
                    }
                }
                try{
                    Thread.sleep(200);
                } catch (InterruptedException e){}
            }
        }
    }
}
