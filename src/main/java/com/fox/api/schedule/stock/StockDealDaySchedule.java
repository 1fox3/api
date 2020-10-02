package com.fox.api.schedule.stock;

import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
import com.fox.api.dao.stock.entity.StockDealDayEntity;
import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.dao.stock.entity.StockPriceDayEntity;
import com.fox.api.dao.stock.mapper.StockDealDayMapper;
import com.fox.api.dao.stock.mapper.StockPriceDayMapper;
import com.fox.api.entity.po.third.stock.StockDayLinePo;
import com.fox.api.entity.po.third.stock.StockDealDayPo;
import com.fox.api.entity.po.third.stock.StockDealPo;
import com.fox.api.service.third.stock.nets.api.NetsDayCsv;
import com.fox.api.service.third.stock.nets.api.NetsDayLine;
import com.fox.api.util.DateUtil;
import com.fox.api.util.StockUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 股票按天交易
 * @author lusongsong
 * @date 2020/4/7 14:22
 */
@Component
public class StockDealDaySchedule extends StockBaseSchedule {
    @Autowired
    private StockDealDayMapper stockDealDayMapper;
    @Autowired
    private StockPriceDayMapper stockPriceDayMapper;
    private static int startYear = 1990;
    private int endYear = Integer.valueOf(DateUtil.getCurrentYear());
    private String currentDate = DateUtil.getCurrentDate();
    private String preDate = DateUtil.getRelateDate(0, -1, 0, DateUtil.DATE_FORMAT_1);
    private NetsDayLine netsDayLine = new NetsDayLine();
    private NetsDayCsv netsDayCsv = new NetsDayCsv();
    /**
     * 复权类型
     */
    Map<String, Integer> fqTypeMap = new LinkedHashMap<String, Integer>(){{
        //put("kline", 0);
        put("klinederc", 1);
    }};
    /**
     * 是否全量同步
     */
    private boolean syncTotal = true;

    /**
     * 创建影子表
     */
    private void createShadowTable() {
        stockPriceDayMapper.createShadowTable();
        stockDealDayMapper.createShadowTable();
    }

    /**
     * 数据表重命名
     */
    private void shadowTableConvert() {
        stockPriceDayMapper.shadowTableConvert();
        stockDealDayMapper.shadowTableConvert();
    }

    /**
     * 删除影子表
     */
    private void dropShadowTable() {
        try {
            stockPriceDayMapper.dropShadow();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            stockDealDayMapper.dropShadow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 同步价格信息
     * @param stockEntity
     */
    private void syncPriceDay(StockEntity stockEntity) {
        BigDecimal preClosePrice = new BigDecimal(0);
        int scanStartYear = syncTotal ? startYear : endYear;
        for (String fqType : fqTypeMap.keySet()) {
            for (int year = scanStartYear; year <= endYear; year += 1) {
                String startDate = "";
                String endDate = "";
                try {
                    startDate = syncTotal ? year + "-01-01" : preDate;
                    endDate = syncTotal ? year + "-12-31" : currentDate;
                    Map<String, String> netsParams = StockUtil.getNetsStockInfoMap(stockEntity);
                    netsParams.put("rehabilitationType", fqType);
                    StockDayLinePo stockDayLinePo = netsDayLine.getDayLine(netsParams, startDate, endDate);
                    if (null == stockDayLinePo.getLineNode()) {
                        continue;
                    }
                    List<StockPriceDayEntity> list = new LinkedList<>();
                    for (StockDealPo stockDealPo : stockDayLinePo.getLineNode()) {
                        StockPriceDayEntity stockPriceDayEntity = new StockPriceDayEntity();
                        stockPriceDayEntity.setStockId(stockEntity.getId());
                        stockPriceDayEntity.setDt(stockDealPo.getDateTime());
                        stockPriceDayEntity.setFqType(fqTypeMap.get(fqType));
                        stockPriceDayEntity.setOpenPrice(BigDecimal.valueOf(stockDealPo.getOpenPrice()));
                        stockPriceDayEntity.setClosePrice(BigDecimal.valueOf(stockDealPo.getClosePrice()));
                        stockPriceDayEntity.setHighestPrice(BigDecimal.valueOf(stockDealPo.getHighestPrice()));
                        stockPriceDayEntity.setLowestPrice(BigDecimal.valueOf(stockDealPo.getLowestPrice()));
                        stockPriceDayEntity.setPreClosePrice(preClosePrice);
                        preClosePrice = BigDecimal.valueOf(stockDealPo.getClosePrice());
                        if (syncTotal) {
                            list.add(stockPriceDayEntity);
                        } else {
                            if (currentDate.equals(stockDealPo.getDateTime())) {
                                StockPriceDayEntity dbStockPriceDayEntity = stockPriceDayMapper.getBySignalDate(stockPriceDayEntity);
                                if (null == dbStockPriceDayEntity) {
                                    stockPriceDayMapper.insert(stockPriceDayEntity);
                                } else {
                                    stockPriceDayEntity.setId(dbStockPriceDayEntity.getId());
                                    stockPriceDayMapper.update(stockPriceDayEntity);
                                }
                            }
                        }
                    }
                    if (list.size() > 0) {
                        stockPriceDayMapper.batchInsert(list);
                    }
                } catch (Exception e) {
                    System.out.println(startDate);
                    System.out.println(stockEntity);
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 同步交易信息
     * @param stockEntity
     */
    private void syncDealDay(StockEntity stockEntity) {
        BigDecimal preClosePrice = new BigDecimal(0);
        int scanStartYear = syncTotal ? startYear : endYear;
        for (int year = scanStartYear; year <= endYear; year += 1) {
            String startDate = "";
            String endDate = "";
            try {
                startDate = syncTotal ? year + "-01-01" : preDate;
                endDate = syncTotal ? year + "-12-31" : currentDate;
                Map<String, String> netsParams = StockUtil.getNetsStockInfoMap(stockEntity);
                netsParams.put("startDate", startDate);
                netsParams.put("endDate", endDate);
                List<StockDealDayPo> stockDealDayPoList = netsDayCsv.getDealDayInfo(netsParams);
                if (null == stockDealDayPoList || stockDealDayPoList.size() == 0) {
                    continue;
                }
                List<StockPriceDayEntity> stockPriceDayEntityList = new LinkedList<>();
                List<StockDealDayEntity> stockDealDayEntityList = new LinkedList<>();
                for (StockDealDayPo stockDealDayPo : stockDealDayPoList) {
                    StockPriceDayEntity stockPriceDayEntity = new StockPriceDayEntity();
                    stockPriceDayEntity.setStockId(stockEntity.getId());
                    stockPriceDayEntity.setDt(stockDealDayPo.getDt());
                    stockPriceDayEntity.setFqType(0);
                    stockPriceDayEntity.setOpenPrice(stockDealDayPo.getOpenPrice());
                    stockPriceDayEntity.setClosePrice(stockDealDayPo.getClosePrice());
                    stockPriceDayEntity.setHighestPrice(stockDealDayPo.getHighestPrice());
                    stockPriceDayEntity.setLowestPrice(stockDealDayPo.getLowestPrice());
                    stockPriceDayEntity.setPreClosePrice(preClosePrice);
                    preClosePrice = stockDealDayPo.getClosePrice();
                    if (syncTotal) {
                        stockPriceDayEntityList.add(stockPriceDayEntity);
                    } else {
                        if (currentDate.equals(stockDealDayPo.getDt())) {
                            StockPriceDayEntity dbStockPriceDayEntity = stockPriceDayMapper.getBySignalDate(stockPriceDayEntity);
                            if (null == dbStockPriceDayEntity) {
                                stockPriceDayMapper.insert(stockPriceDayEntity);
                            } else {
                                stockPriceDayEntity.setId(dbStockPriceDayEntity.getId());
                                stockPriceDayMapper.update(stockPriceDayEntity);
                            }
                        }
                    }

                    StockDealDayEntity stockDealDayEntity = new StockDealDayEntity();
                    stockDealDayEntity.setStockId(stockEntity.getId());
                    stockDealDayEntity.setDt(stockDealDayPo.getDt());
                    stockDealDayEntity.setClosePrice(stockDealDayPo.getClosePrice());
                    stockDealDayEntity.setDealNum(stockDealDayPo.getDealNum());
                    stockDealDayEntity.setDealMoney(stockDealDayPo.getDealMoney());
                    stockDealDayEntity.setTotalEquity(
                            null == stockDealDayPo.getTotalValue() || 0 == stockDealDayPo.getClosePrice().compareTo(BigDecimal.ZERO)
                                    ? 0 : stockDealDayPo.getTotalValue().divide(stockDealDayPo.getClosePrice(), 2, RoundingMode.HALF_UP).longValue()
                    );
                    stockDealDayEntity.setCircEquity(
                            null == stockDealDayPo.getCircValue() || 0 == stockDealDayPo.getClosePrice().compareTo(BigDecimal.ZERO)
                                    ? 0 : stockDealDayPo.getCircValue().divide(stockDealDayPo.getClosePrice(), 2, RoundingMode.HALF_UP).longValue()
                    );
                    if (syncTotal) {
                        stockDealDayEntityList.add(stockDealDayEntity);
                    } else {
                        if (currentDate.equals(stockDealDayPo.getDt())) {
                            StockDealDayEntity dbStockDealDayEntity = stockDealDayMapper.getBySignalDate(stockDealDayEntity);
                            if (null == dbStockDealDayEntity) {
                                stockDealDayMapper.insert(stockDealDayEntity);
                            } else {
                                stockDealDayEntity.setId(dbStockDealDayEntity.getId());
                                stockDealDayMapper.update(stockDealDayEntity);
                            }
                        }
                    }
                }

                if (stockPriceDayEntityList.size() > 0 && stockDealDayEntityList.size() > 0){
                    stockPriceDayMapper.batchInsert(stockPriceDayEntityList);
                    stockDealDayMapper.batchInsert(stockDealDayEntityList);
                }
            } catch (Exception e) {
                System.out.println(startDate);
                System.out.println(stockEntity);
                e.printStackTrace();
            }
        }
    }

    /**
     * 遍历所有需同步的股票列表
     */
    private void stockScanSync() {
        //同步重点指标
        List<Integer> topIndexList = this.stockProperty.getTopIndex();
        for (Integer id : topIndexList) {
            StockEntity stockEntity = this.stockMapper.getById(id);
            System.out.println(stockEntity);
            this.syncPriceDay(stockEntity);
            this.syncDealDay(stockEntity);
        }
        //同步股票
        int stockId = 0;
        Integer onceLimit = 100;
        while (true) {
            List<StockEntity> stockEntityList = this.stockMapper.getTotalByType(
                    2,
                    stockId,
                    onceLimit.toString()
            );
            if (null == stockEntityList) {
                break;
            }
            for (StockEntity stockEntity : stockEntityList) {
                if (null == stockEntity) {
                    continue;
                }
                stockId = null == stockEntity.getId() ? stockEntity.getId() : stockId + 1;
                if (null == stockEntity.getNetsStockCode() || 0 == stockEntity.getNetsStockCode().length()) {
                    continue;
                }
                this.syncPriceDay(stockEntity);
                this.syncDealDay(stockEntity);
            }
            if (stockEntityList.size() < onceLimit) {
                break;
            }
        }
    }

    /**
     * 同步所有的按天交易信息数据
     */
    @LogShowTimeAnt
    public void syncTotalDealDayInfo() {
        syncTotal = true;
        try {
            this.dropShadowTable();
            this.createShadowTable();
            this.stockScanSync();
            this.shadowTableConvert();
            this.dropShadowTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 同步交易当天信息数据
     */
    @LogShowTimeAnt
    public void syncCurrentDealDayInfo() {
        syncTotal = false;
        try {
            this.stockScanSync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
