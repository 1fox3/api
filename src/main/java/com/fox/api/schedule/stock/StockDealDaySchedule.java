package com.fox.api.schedule.stock;

import com.alibaba.fastjson.JSONObject;
import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
import com.fox.api.dao.stock.entity.StockDealDayEntity;
import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.dao.stock.entity.StockInfoEntity;
import com.fox.api.dao.stock.mapper.StockDealDayMapper;
import com.fox.api.schedule.stock.handler.StockScheduleHandler;
import com.fox.api.util.DateUtil;
import com.fox.api.util.FileUtil;
import com.fox.spider.stock.api.nets.NetsDayDealInfoApi;
import com.fox.spider.stock.api.nets.NetsFQKLineDataApi;
import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.po.nets.NetsDayDealInfoPo;
import com.fox.spider.stock.entity.po.nets.NetsFQKLineDataPo;
import com.fox.spider.stock.entity.po.nets.NetsFQKLineNodeDataPo;
import com.fox.spider.stock.entity.vo.StockVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.text.ParseException;
import java.util.*;

/**
 * 股票按天交易数据
 *
 * @author lusongsong
 * @date 2020/4/7 14:22
 */
@Component
public class StockDealDaySchedule extends StockBaseSchedule implements StockScheduleHandler {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 按天交易数据库操作类
     */
    @Autowired
    StockDealDayMapper stockDealDayMapper;
    /**
     * 网易股票按天交易日数据接口
     */
    @Autowired
    NetsDayDealInfoApi netsDayDealInfoApi;
    /**
     * 网易股票按天交易价格数据接口
     */
    @Autowired
    NetsFQKLineDataApi netsFQKLineDataApi;
    /**
     * 默认股票数据接口结果保存路径
     */
    private static final String DEFAULT_STOCK_API_DATA_PATH = "/api";
    /**
     * 稳定数据日期
     */
    String stableDate = DateUtil.getRelateDate(0, 0, -60, DateUtil.DATE_FORMAT_1);
    /**
     * 默认数据同步开始年份
     */
    String defaultStartYear;
    /**
     * 数据同步开始年份
     */
    String startYear;
    /**
     * 数据同步结束年份
     */
    String endYear;
    /**
     * 数据同步开始日期
     */
    private String startDate;
    /**
     * 数据同步结束日期
     */
    private String endDate = DateUtil.getCurrentDate();
    /**
     * 复权类型
     */
    List<Integer> fqTypeList = Arrays.asList(
            StockConst.SFQ_BEFORE,
            StockConst.SFQ_AFTER
    );
    /**
     * 是否全量同步
     */
    private boolean syncTotal = true;

    /**
     * 创建影子表
     */
    private void createShadow() {
        try {
            stockDealDayMapper.createShadow();
        } catch (Exception e) {
            logger.error("createShadow", e);
        }
    }

    /**
     * 数据表重命名
     */
    private void shadowConvert() {
        try {
            stockDealDayMapper.shadowConvert();
        } catch (Exception e) {
            logger.error("shadowConvert", e);
        }
    }

    /**
     * 删除影子表
     */
    private void dropShadow() {
        try {
            stockDealDayMapper.dropShadow();
        } catch (Exception e) {
            logger.error("dropShadow", e);
        }
    }

    /**
     * 数据表重命名
     */
    private void optimizeTable() {
        try {
            stockDealDayMapper.optimize();
        } catch (Exception e) {
            logger.error("optimizeTable", e);
        }
    }

    /**
     * 获取股票数据保存目录
     *
     * @param stockVo
     * @return
     */
    private String getStockDataPath(StockVo stockVo) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("/");
        stringBuffer.append(stockVo.getStockMarket());
        String stockCode = stockVo.getStockCode();
        for (int i = 0; i < stockCode.length(); i++) {
            if (0 == i % 2) {
                stringBuffer.append("/");
            }
            stringBuffer.append(stockCode.charAt(i));
        }
        stringBuffer.append("/");
        return stringBuffer.toString();
    }

    /**
     * 获取股票数据日期保存目录
     *
     * @param stockVo
     * @param date
     * @return
     */
    private String getStockDataPath(StockVo stockVo, String date) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(getStockDataPath(stockVo));
        stringBuffer.append(DateUtil.dateStrFormatChange(date, DateUtil.DATE_FORMAT_1, DateUtil.YEAR_FORMAT_1));
        stringBuffer.append(".txt");
        return stringBuffer.toString();
    }

    /**
     * 股票按天交易数据接口结果保存路径
     *
     * @param className
     * @param stockVo
     * @param startDate
     * @return
     */
    private String getDealInfoDataPath(String className, StockVo stockVo, String startDate) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(DEFAULT_STOCK_API_DATA_PATH);
        stringBuffer.append("/");
        stringBuffer.append(className);
        stringBuffer.append(getStockDataPath(stockVo, startDate));
        return stringBuffer.toString();
    }

    /**
     * 获取交易数据
     *
     * @param stockVo
     * @param startDate
     * @param endDate
     * @return
     */
    private List<NetsDayDealInfoPo> getDayDealInfoList(StockVo stockVo, String startDate, String endDate) {
        List<NetsDayDealInfoPo> netsDayDealInfoPoList = null;
        try {
            String filePath = null;
            if (syncTotal && (DateUtil.compare(endDate, stableDate, DateUtil.DATE_FORMAT_1) <= 0)) {
                filePath = getDealInfoDataPath(netsDayDealInfoApi.getClass().getSimpleName(), stockVo, startDate);
                String dealString = FileUtil.read(filePath);
                if (null != dealString && !dealString.isEmpty()) {
                    netsDayDealInfoPoList = JSONObject.parseArray(dealString, NetsDayDealInfoPo.class);
                }
            }
            if (null == netsDayDealInfoPoList || netsDayDealInfoPoList.isEmpty()) {
                netsDayDealInfoPoList = netsDayDealInfoApi.dayDealInfo(
                        stockVo, startDate, endDate
                );
                if (null != filePath && !filePath.isEmpty() && null != netsDayDealInfoPoList
                        && !netsDayDealInfoPoList.isEmpty()) {
                    FileUtil.coverWrite(filePath, JSONObject.toJSONString(netsDayDealInfoPoList));
                }
            }
        } catch (ParseException e) {
            logger.error("getDayDealInfoList", e);
        }
        return netsDayDealInfoPoList;
    }

    private String getPriceInfoDataPath(String className, StockVo stockVo, String startDate, Integer fqType) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(DEFAULT_STOCK_API_DATA_PATH);
        stringBuffer.append("/");
        stringBuffer.append(className);
        stringBuffer.append("/");
        stringBuffer.append(fqType);
        stringBuffer.append("/");
        stringBuffer.append(getStockDataPath(stockVo, startDate));
        return stringBuffer.toString();
    }

    /**
     * 获取价格数据
     *
     * @param stockVo
     * @param startDate
     * @param endDate
     * @param fqType
     * @return
     */
    private NetsFQKLineDataPo getDayPriceInfoList(
            StockVo stockVo, String startDate, String endDate, Integer fqType
    ) {
        NetsFQKLineDataPo netsFQKLineDataPo = null;
        try {
            String filePath = null;
            if (syncTotal && (DateUtil.compare(endDate, stableDate, DateUtil.DATE_FORMAT_1) <= 0)) {
                filePath = getPriceInfoDataPath(netsFQKLineDataApi.getClass().getSimpleName(), stockVo, startDate, fqType);
                String priceString = FileUtil.read(filePath);
                if (null != priceString && !priceString.isEmpty()) {
                    netsFQKLineDataPo = JSONObject.parseObject(priceString, NetsFQKLineDataPo.class);
                }
            }
            if (null == netsFQKLineDataPo) {
                netsFQKLineDataPo = netsFQKLineDataApi.fqKLineData(stockVo, startDate, endDate, fqType);
                if (null != filePath && !filePath.isEmpty() && null != netsFQKLineDataPo) {
                    FileUtil.coverWrite(filePath, JSONObject.toJSONString(netsFQKLineDataPo));
                }
            }
        } catch (ParseException e) {
            logger.error("getDayPriceInfoList", e);
        }
        return netsFQKLineDataPo;
    }

    /**
     * 同步按天交易数据
     *
     * @param stockEntity
     */
    private void syncDealDay(StockEntity stockEntity) throws ParseException {
        if (null == stockEntity || null == stockEntity.getId() || null == stockEntity.getStockMarket()
                || null == stockEntity.getStockCode()) {
            return;
        }
        StockVo stockVo = new StockVo(stockEntity.getStockCode(), stockEntity.getStockMarket());
        List<NetsDayDealInfoPo> netsDayDealInfoPoList = getDayDealInfoList(
                stockVo, startDate, endDate
        );
        if (null == netsDayDealInfoPoList || netsDayDealInfoPoList.isEmpty()) {
            return;
        }
        for (Integer fqType : fqTypeList) {
            List<StockDealDayEntity> stockDealDayEntityList = new ArrayList<>();
            if (StockConst.SFQ_AFTER == fqType) {
                for (NetsDayDealInfoPo netsDayDealInfoPo : netsDayDealInfoPoList) {
                    if (null == netsDayDealInfoPo) {
                        continue;
                    }
                    try {
                        StockDealDayEntity stockDealDayEntity = new StockDealDayEntity();
                        stockDealDayEntity.setStockId(stockEntity.getId());
                        stockDealDayEntity.setDt(netsDayDealInfoPo.getDt());
                        stockDealDayEntity.setFqType(fqType);
                        stockDealDayEntity.setOpenPrice(netsDayDealInfoPo.getOpenPrice());
                        stockDealDayEntity.setClosePrice(netsDayDealInfoPo.getClosePrice());
                        stockDealDayEntity.setHighestPrice(netsDayDealInfoPo.getHighestPrice());
                        stockDealDayEntity.setLowestPrice(netsDayDealInfoPo.getLowestPrice());
                        stockDealDayEntity.setDealNum(netsDayDealInfoPo.getDealNum());
                        stockDealDayEntity.setPreClosePrice(netsDayDealInfoPo.getPreClosePrice());
                        stockDealDayEntity.setDealMoney(netsDayDealInfoPo.getDealMoney());
                        stockDealDayEntity.setCircEquity(
                                netsDayDealInfoPo.getCircValue()
                                        .divide(netsDayDealInfoPo.getClosePrice(), 2, RoundingMode.HALF_UP)
                                        .longValue()
                        );
                        stockDealDayEntity.setTotalEquity(
                                netsDayDealInfoPo.getTotalValue()
                                        .divide(netsDayDealInfoPo.getClosePrice(), 2, RoundingMode.HALF_UP)
                                        .longValue()
                        );
                        stockDealDayEntityList.add(stockDealDayEntity);
                    } catch (ArithmeticException e) {
                        logger.error(stockEntity.getId().toString() + netsDayDealInfoPo.toString());
                        logger.error(stockEntity.getId().toString(), e);
                    }
                }
            } else {
                if (DateUtil.compare(startDate, "2015-01-01", DateUtil.DATE_FORMAT_1) < 0) {
                    continue;
                }
                Map<String, NetsDayDealInfoPo> dateDealInfoMap = new HashMap<>(netsDayDealInfoPoList.size());
                for (NetsDayDealInfoPo netsDayDealInfoPo : netsDayDealInfoPoList) {
                    if (null != netsDayDealInfoPo && null != netsDayDealInfoPo.getDt()) {
                        dateDealInfoMap.put(netsDayDealInfoPo.getDt(), netsDayDealInfoPo);
                    }
                }
                NetsFQKLineDataPo netsFQKLineDataPo = getDayPriceInfoList(stockVo, startDate, endDate, fqType);
                if (null == netsFQKLineDataPo || null == netsFQKLineDataPo.getKlineData()) {
                    continue;
                }
                List<NetsFQKLineNodeDataPo> netsFQKLineNodeDataPoList = netsFQKLineDataPo.getKlineData();

                for (NetsFQKLineNodeDataPo netsFQKLineNodeDataPo : netsFQKLineNodeDataPoList) {
                    if (null == netsFQKLineNodeDataPo) {
                        continue;
                    }
                    NetsDayDealInfoPo netsDayDealInfoPo = dateDealInfoMap.containsKey(netsFQKLineNodeDataPo.getDt()) ?
                            dateDealInfoMap.get(netsFQKLineNodeDataPo.getDt()) : null;
                    if (null != netsDayDealInfoPo) {
                        try {
                            StockDealDayEntity stockDealDayEntity = new StockDealDayEntity();
                            stockDealDayEntity.setStockId(stockEntity.getId());
                            stockDealDayEntity.setDt(netsFQKLineNodeDataPo.getDt());
                            stockDealDayEntity.setFqType(fqType);
                            stockDealDayEntity.setOpenPrice(netsFQKLineNodeDataPo.getOpenPrice());
                            stockDealDayEntity.setClosePrice(netsFQKLineNodeDataPo.getClosePrice());
                            stockDealDayEntity.setHighestPrice(netsFQKLineNodeDataPo.getHighestPrice());
                            stockDealDayEntity.setLowestPrice(netsFQKLineNodeDataPo.getLowestPrice());
                            stockDealDayEntity.setDealNum(netsFQKLineNodeDataPo.getDealNum());
                            stockDealDayEntity.setPreClosePrice(netsDayDealInfoPo.getPreClosePrice());
                            stockDealDayEntity.setDealMoney(netsDayDealInfoPo.getDealMoney());
                            stockDealDayEntity.setCircEquity(
                                    netsDayDealInfoPo.getCircValue()
                                            .divide(netsDayDealInfoPo.getClosePrice(), 2, RoundingMode.HALF_UP)
                                            .longValue()
                            );
                            stockDealDayEntity.setTotalEquity(
                                    netsDayDealInfoPo.getTotalValue()
                                            .divide(netsDayDealInfoPo.getClosePrice(), 2, RoundingMode.HALF_UP)
                                            .longValue()
                            );
                            stockDealDayEntityList.add(stockDealDayEntity);
                        } catch (ArithmeticException e) {
                            logger.error(stockEntity.getId().toString() + netsDayDealInfoPo.toString());
                            logger.error(stockEntity.getId().toString() + netsFQKLineNodeDataPo.toString());
                            logger.error(stockEntity.getId().toString(), e);
                        }
                    }
                }
            }
            if (!stockDealDayEntityList.isEmpty()) {
                if (syncTotal) {
                    try {
                        stockDealDayMapper.batchInsert(stockDealDayEntityList);
                    } catch (Exception e) {
                        logger.error("batchInsert", e);
                        logger.error("rows", stockDealDayEntityList);
                    }
                } else {
                    for (StockDealDayEntity stockDealDayEntity : stockDealDayEntityList) {
                        StockDealDayEntity dbStockDealDayEntity = stockDealDayMapper.getBySignalDate(stockDealDayEntity);
                        if (null != dbStockDealDayEntity) {
                            stockDealDayEntity.setId(dbStockDealDayEntity.getId());
                            stockDealDayMapper.update(stockDealDayEntity);
                        } else {
                            stockDealDayMapper.insert(stockDealDayEntity);
                        }
                    }
                }
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
            dropShadow();
            createShadow();
            endYear = DateUtil.getCurrentYear();
            if (null == endYear) {
                return;
            }
            for (Integer sm : StockConst.SM_A_LIST) {
                //交易开始日期
                defaultStartYear = StockConst.stockMarketStartYear(sm);
                if (null == defaultStartYear) {
                    continue;
                }
                //同步股票
                stockMarketScan(sm, this);
            }
            shadowConvert();
            dropShadow();
            optimizeTable();
        } catch (Exception e) {
            logger.error("syncTotalDealDayInfo", e);
        }
    }

    /**
     * 同步交易当天信息数据
     */
    @LogShowTimeAnt
    public void syncCurrentDealDayInfo() {
        syncTotal = false;
        try {
            //交易开始年份
            startDate = DateUtil.getRelateDate(0, 0, -60, DateUtil.DATE_FORMAT_1);
            for (Integer sm : StockConst.SM_A_LIST) {
                // 检查今年的数据是否具备
                String curYear = DateUtil.getCurrentYear();
                List<NetsDayDealInfoPo> netsDayDealInfoPoList = getDayDealInfoList(
                        demoStock.get(sm), curYear + "-01-01", curYear + "-12-31"
                );
                if (null == netsDayDealInfoPoList || netsDayDealInfoPoList.isEmpty()) {
                    endDate = (Integer.valueOf(curYear) - 1) + "-12-31";
                    if (DateUtil.compare(startDate, endDate, DateUtil.DATE_FORMAT_1) >= 0) {
                        continue;
                    }
                }
                //同步股票
                stockMarketScan(sm, this);
            }
        } catch (Exception e) {
            logger.error("syncCurrentDealDayInfo", e);
        }
    }

    /**
     * 处理单只股票
     *
     * @param stockEntity
     */
    @Override
    public void handle(StockEntity stockEntity) {
        if (null == stockEntity || null == stockEntity.getStockCode()
                || stockEntity.getStockCode().isEmpty()) {
            return;
        }
        //如果是同步全部交易数据，则获取股票的具体上市时间
        if (syncTotal) {
            startYear = defaultStartYear;
            StockInfoEntity stockInfoEntity = stockInfoMapper.getByStockId(stockEntity.getId());
            if (null != stockInfoEntity && null != stockInfoEntity.getStockOnDate()) {
                startYear = DateUtil.dateStrFormatChange(
                        stockInfoEntity.getStockOnDate(), DateUtil.DATE_FORMAT_1, DateUtil.YEAR_FORMAT_1
                );
            }
            Integer startYearNum = Integer.valueOf(startYear);
            Integer endYearNum = Integer.valueOf(endYear);
            for (Integer year = startYearNum; year <= endYearNum; year++) {
                startDate = year + "-01-01";
                endDate = year + "-12-31";
                try {
                    syncDealDay(stockEntity);
                } catch (Exception e) {
                    logger.error("syncTotal", e);
                }

            }
        } else {
            try {
                syncDealDay(stockEntity);
            } catch (Exception e) {
                logger.error("syncCurrent", e);
            }
        }
    }
}
