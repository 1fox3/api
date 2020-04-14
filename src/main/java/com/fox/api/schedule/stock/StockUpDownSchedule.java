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
import com.fox.api.service.stock.StockOfflineService;
import com.fox.api.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class StockUpDownSchedule extends StockBaseSchedule {
    @Autowired
    private StockUpDownMapper stockUpDownMapper;

    @Autowired
    private StockLimitUpDownMapper stockLimitUpDownMapper;

    @Autowired
    private StockOfflineService stockOfflineService;

    /**
     * 执行时间请设置在股市结束之后，因为接口会反回当天数据，影响涨跌停的判断
     */
    @LogShowTimeAnt
    @Scheduled(cron="0 5 15 * * 1-5")
    public void stockUpDown() {
        int bigId = stockMapper.getLastId();
        List<Integer> scopeList = Arrays.asList(10, 30, 50, 100, 200, 300);
        int limitLen = scopeList.get(scopeList.size() - 1);

        for (int i = 1; i <= bigId; i++) {
            Double currentPrice , highestPrice, lowestPrice, todayPrice, yesterdayPrice;
            currentPrice = highestPrice = lowestPrice = todayPrice = yesterdayPrice = 0.0;
            String today, yesterday;
            today = yesterday = "";
            StockEntity stockEntity = stockMapper.getById(i);
            if (null == stockEntity || stockEntity.getNetsStockCode().equals("")) {
                continue;
            }

            StockUpDownEntity stockUpDownEntity = stockUpDownMapper.getByStockId(stockEntity.getId());
            StockLimitUpDownEntity stockLimitUpDownEntity =
                    stockLimitUpDownMapper.getByStockId(stockEntity.getId());

            if (3 == stockEntity.getStockMarket() //过滤掉港股
                    || 1 == stockEntity.getStockStatus() //过滤掉已退市的
                    || 2 != stockEntity.getStockType() //过滤掉不是股票类型的
            ) {
                if (null != stockUpDownEntity) {
                    stockUpDownMapper.deleteById(stockUpDownEntity.getId());
                }
                if (null != stockLimitUpDownEntity) {
                    stockLimitUpDownMapper.deleteById(stockLimitUpDownEntity.getId());
                }
                continue;
            }
            if (null == stockUpDownEntity) {
                stockUpDownEntity = new StockUpDownEntity();
            }
            stockUpDownEntity.setStockId(stockEntity.getId());
            if (stockEntity.getId() > 0) {
                StockDealDayLineDto stockDealDayLineDto = stockOfflineService.line(stockEntity.getId(), DateUtil.getRelateDate(-2, 0, 0, DateUtil.DATE_FORMAT_1));
                List<StockDealDayDto> stockDealDayDtos = stockDealDayLineDto.getLineNode();
                if (null == stockDealDayDtos) {
                    continue;
                }
                int len = stockDealDayDtos.size();
                if (0 == len) {
                    continue;
                }
                for (int j = 0; j<= limitLen; j++) {
                    int pos = len - j - 1;
                    if (pos >= 0) {
                        StockDealDayDto stockDealDayDto = stockDealDayDtos.get(pos);
                        currentPrice = 0.0 == currentPrice ? stockDealDayDto.getClosePrice() : currentPrice;
                        if (0 == j) {
                            today = stockDealDayDto.getDt();
                            todayPrice = stockDealDayDto.getClosePrice();
                        }
                        if (1 == j) {
                            yesterday = stockDealDayDto.getDt();
                            yesterdayPrice = stockDealDayDto.getClosePrice();
                        }

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
                if (todayPrice > 0 && yesterdayPrice > 0) {
                    //type值说明 0-正常，1-涨停，2-跌停
                    Integer type = 0;
                    if ((float)Math.abs((todayPrice - yesterdayPrice) * 100) / 100 == (float)Math.round(yesterdayPrice * 0.1 * 100) / 100) {
                        type = todayPrice > yesterdayPrice ? 1 : 2;
                    }

                    if (null != stockLimitUpDownEntity) {
                        if (0 != type) {
                            if (stockLimitUpDownEntity.getType() != type) {
                                stockLimitUpDownEntity.setStartPrice(Float.valueOf(String.valueOf(yesterdayPrice)));
                                stockLimitUpDownEntity.setStartDate(today);
                                stockLimitUpDownEntity.setNum(1);
                            } else {
                                stockLimitUpDownEntity.setNum(stockLimitUpDownEntity.getNum() + 1);
                            }
                            stockLimitUpDownEntity.setCurrentPrice(Float.valueOf(String.valueOf(todayPrice)));
                            stockLimitUpDownEntity.setCurrentDate(today);
                        }
                        stockLimitUpDownEntity.setType(type);
                        stockLimitUpDownMapper.updateById(stockLimitUpDownEntity);
                    } else {
                        if (0 != type) {
                            stockLimitUpDownEntity = new StockLimitUpDownEntity();
                            stockLimitUpDownEntity.setStockId(stockEntity.getId());
                            stockLimitUpDownEntity.setType(type);
                            stockLimitUpDownEntity.setNum(1);
                            stockLimitUpDownEntity.setCurrentPrice(Float.valueOf(String.valueOf(todayPrice)));
                            stockLimitUpDownEntity.setCurrentDate(today);
                            stockLimitUpDownEntity.setStartPrice(Float.valueOf(String.valueOf(yesterdayPrice)));
                            stockLimitUpDownEntity.setStartDate(yesterday);
                            stockLimitUpDownMapper.insert(stockLimitUpDownEntity);
                        }
                    }
                }
            }
            if (null == stockUpDownEntity.getId()) {
                stockUpDownMapper.insert(stockUpDownEntity);
            } else {
                stockUpDownMapper.updateById(stockUpDownEntity);
            };
            try{
                Thread.sleep(200);
            } catch (InterruptedException e){}
        }
    }
}
