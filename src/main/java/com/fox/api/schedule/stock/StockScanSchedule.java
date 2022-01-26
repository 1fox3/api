package com.fox.api.schedule.stock;

import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.util.StockUtil;
import com.fox.spider.stock.api.sina.SinaBaseApi;
import com.fox.spider.stock.api.sina.SinaRealtimeDealInfoApi;
import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.po.sina.SinaRealtimeDealInfoPo;
import com.fox.spider.stock.entity.vo.StockCategoryVo;
import com.fox.spider.stock.entity.vo.StockVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 股票代码扫描
 *
 * @author lusongsong
 * @date 2020/3/5 18:13
 */
@Component
public class StockScanSchedule extends StockBaseSchedule {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 新浪实时交易数据接口
     */
    @Autowired
    SinaRealtimeDealInfoApi sinaRealtimeDealInfoApi;
    /**
     * 股票缓存数据同步任务
     */
    @Autowired
    StockIntoListSchedule stockIntoListSchedule;
    /**
     * 单次扫描股票代码数
     */
    private static Integer SCAN_ONCE_LIMIT = 100;
    /**
     * 最新交易日
     */
    private String lastDealDate;
    /**
     * 股票代码扫描范围
     */
    private static Map<Integer, Integer> STOCK_SCAN_SCOPE_CONFIG = new LinkedHashMap<Integer, Integer>() {{
        put(StockConst.SM_SH, 1000000);
        put(StockConst.SM_SZ, 1000000);
        put(StockConst.SM_HK, 100000);
    }};
    /**
     * 暂未发现扫描规律的股票代码
     */
    private static Map<Integer, List<String>> SPECIAL_STOCK_CODE_MAP = new LinkedHashMap<Integer, List<String>>() {{
        put(StockConst.SM_HK, Arrays.asList(
                "HSI",//恒生指数
                "HSCEI"//国企指数
        ));
    }};

    /**
     * 扫描股票代码列表
     *
     * @param stockVoList
     */
    private void scanStockCodeList(List<StockVo> stockVoList) {
        Map<String, SinaRealtimeDealInfoPo> stringSinaRealtimeDealInfoPoMap =
                sinaRealtimeDealInfoApi.batchRealtimeDealInfo(stockVoList);
        //当列表全部为空的股票代码时，休眠一段时间
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
        for (StockVo stockVo : stockVoList) {
            if (stringSinaRealtimeDealInfoPoMap.containsKey(stockVo.getStockCode())) {
                SinaRealtimeDealInfoPo sinaRealtimeDealInfoPo =
                        stringSinaRealtimeDealInfoPoMap.get(stockVo.getStockCode());
                if (null != sinaRealtimeDealInfoPo
                        && null != sinaRealtimeDealInfoPo.getStockName()
                        && !sinaRealtimeDealInfoPo.getStockName().equals("")) {
                    syncStockDealStatus(stockVo, sinaRealtimeDealInfoPo);
                }
            }
        }
    }

    /**
     * 分批获取需要扫描的股票代码
     *
     * @param stockMarket
     * @param maxLimit
     */
    private void scanStockMarket(Integer stockMarket, Integer maxLimit) {
        List<StockVo> stockVoList = new ArrayList<>();
        Integer stockCodeLen = String.valueOf(maxLimit).length() - 1;
        Integer cCopies = 0;
        String stockCodePer = "";
        for (Integer i = 0; i < maxLimit; i++) {
            if (i == Math.pow(10, cCopies) || 0 == i) {
                stockCodePer = String.join("", Collections.nCopies(stockCodeLen - cCopies - 1, "0"));
            }
            if (i > Math.pow(10, cCopies)) {
                cCopies++;
            }
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(stockCodePer);
            stringBuffer.append(i);
            stockVoList.add(new StockVo(stringBuffer.toString(), stockMarket));
            if (stockVoList.size() >= SCAN_ONCE_LIMIT || maxLimit.equals(i)) {
                try {
                    this.scanStockCodeList(stockVoList);
                    stockVoList.clear();
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        }
    }

    /**
     * 扫描暂未发现规律的股票代码
     *
     * @param stockMarket
     */
    private void scanSpecialStockCode(Integer stockMarket) {
        if (SPECIAL_STOCK_CODE_MAP.containsKey(stockMarket)) {
            List<String> specialStockCodeList = SPECIAL_STOCK_CODE_MAP.get(stockMarket);
            List<StockVo> stockVoList = new ArrayList<>();
            for (String stockCode : specialStockCodeList) {
                stockVoList.add(new StockVo(stockCode, stockMarket));
            }
            if (!stockVoList.isEmpty()) {
                try {
                    this.scanStockCodeList(stockVoList);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }

        }
    }

    /**
     * 扫描股票代码
     */
    @LogShowTimeAnt
    public void stockCodeScan() {
        for (Integer stockMarket : StockScanSchedule.STOCK_SCAN_SCOPE_CONFIG.keySet()) {
            //任务执行时数据中的交易日期还未更新
            lastDealDate = StockUtil.preDealDate(stockMarket);
            this.scanStockMarket(stockMarket, StockScanSchedule.STOCK_SCAN_SCOPE_CONFIG.get(stockMarket));
            this.scanSpecialStockCode(stockMarket);
        }
        stockMapper.optimize();
    }

    /**
     * 同步股市交易状态
     * @param stockMarket
     */
    @LogShowTimeAnt
    public void syncStockMarketDealStatus(Integer stockMarket) {
        if (StockConst.SM_ALL.contains(stockMarket)) {
            if (StockConst.SM_A == stockMarket) {
                for (Integer sm : StockConst.SM_A_LIST) {
                    stockDealStatusScan(sm);
                }
            } else {
                stockDealStatusScan(stockMarket);
            }
        }
        stockIntoListSchedule.stockCacheInfoRefresh(stockMarket);
    }

    /**
     * 同步交易状态
     */
    private void stockDealStatusScan(Integer stockMarket) {
        if (!StockUtil.todayIsDealDate(stockMarket)) {
            return;
        }
        Integer stockId = 0;
        List<StockVo> stockVoList = new ArrayList<>();
        lastDealDate = StockUtil.lastDealDate(stockMarket);
        try {
            while (true) {
                stockVoList.clear();
                List<StockEntity> stockEntityList = this.stockMapper.getListByType(
                        StockConst.ST_STOCK,
                        stockId,
                        stockMarket,
                        null,
                        SCAN_ONCE_LIMIT.toString()
                );
                if (null == stockEntityList || stockEntityList.isEmpty()) {
                    break;
                }
                for (StockEntity stockEntity : stockEntityList) {
                    if (null == stockEntity) {
                        continue;
                    }
                    StockVo stockVo = new StockVo(stockEntity.getStockCode(), stockEntity.getStockMarket());
                    stockVoList.add(stockVo);
                }
                Map<String, SinaRealtimeDealInfoPo> stringSinaRealtimeDealInfoPoMap =
                        sinaRealtimeDealInfoApi.batchRealtimeDealInfo(stockVoList);
                for (StockEntity stockEntity : stockEntityList) {
                    if (null == stockEntity) {
                        continue;
                    }
                    stockId = null == stockEntity.getId() ? stockId + 1 : stockEntity.getId();
                    String stockCode = stockEntity.getStockCode();
                    if (stringSinaRealtimeDealInfoPoMap.containsKey(stockCode)) {
                        SinaRealtimeDealInfoPo sinaRealtimeDealInfoPo = stringSinaRealtimeDealInfoPoMap.get(stockCode);
                        //本地标记当前无交易或者接口获取的交易状态不正常则更新
                        if (1 == stockEntity.getStockStatus()
                                || !"00".equals(sinaRealtimeDealInfoPo.getDealStatus())) {
                            try {
                                syncEntityStockDealStatus(stockEntity, sinaRealtimeDealInfoPo);
                            } catch (Exception e) {
                                logger.error(Integer.toString(stockId));
                                logger.error(e.getMessage());
                            }
                        }
                    }
                }
                if (stockEntityList.size() < SCAN_ONCE_LIMIT) {
                    break;
                }
            }
        } catch (Exception e) {
            logger.error(Integer.toString(stockId));
            logger.error(e.getMessage());
        }
    }

    /**
     * 同步交易信息
     *
     * @param stockVo
     * @param sinaRealtimeDealInfoPo
     */
    private void syncStockDealStatus(StockVo stockVo, SinaRealtimeDealInfoPo sinaRealtimeDealInfoPo) {
        if (null == stockVo || null == sinaRealtimeDealInfoPo) {
            return;
        }
        StockEntity stockEntity = stockMapper.getByStockCode(stockVo.getStockCode(), stockVo.getStockMarket());
        if (null == stockEntity) {
            stockEntity = new StockEntity();
        }
        stockEntity.setStockCode(stockVo.getStockCode());
        stockEntity.setStockMarket(stockVo.getStockMarket());
        syncEntityStockDealStatus(stockEntity, sinaRealtimeDealInfoPo);
    }

    /**
     * 同步股票实例信息
     *
     * @param stockEntity
     * @param sinaRealtimeDealInfoPo
     */
    private void syncEntityStockDealStatus(StockEntity stockEntity, SinaRealtimeDealInfoPo sinaRealtimeDealInfoPo) {
        if (null == stockEntity || null == sinaRealtimeDealInfoPo || null == sinaRealtimeDealInfoPo.getStockName()
                || sinaRealtimeDealInfoPo.getStockName().isEmpty()) {
            return;
        }
        stockEntity.setStockName(sinaRealtimeDealInfoPo.getStockName());
        stockEntity.setStockNameEn(null == sinaRealtimeDealInfoPo.getStockNameEn()
                ? "" : sinaRealtimeDealInfoPo.getStockNameEn());

        //股票状态
        Integer stockStatus = 0;
        if (!lastDealDate.equals(sinaRealtimeDealInfoPo.getDt())
                || SinaBaseApi.NO_DEAL_STATUS_LIST.contains(sinaRealtimeDealInfoPo.getDealStatus())
        ) {
            stockStatus = 1;
        }
        stockEntity.setStockStatus(stockStatus);
        //判定类别
        StockCategoryVo stockCategoryVo = StockConst.stockCategory(
                stockEntity.getStockCode(), stockEntity.getStockMarket()
        );
        //处理港股中的基金分类异常
        if (StockConst.SM_HK == stockEntity.getStockMarket()
                && stockCategoryVo.getStockType() == StockConst.ST_STOCK
                && (
                        sinaRealtimeDealInfoPo.getStockName().endsWith("基金")
                                || sinaRealtimeDealInfoPo.getStockName().endsWith("信托")
                                || sinaRealtimeDealInfoPo.getStockName().equals("招商局商业房托"))
        ) {
            stockCategoryVo = new StockCategoryVo(
                    StockConst.SM_HK,
                    StockConst.ST_FUND,
                    StockConst.SK_HK_FUND);
        }
        stockEntity.setStockType(null == stockCategoryVo.getStockType() ?
                StockConst.ST_UNKNOWN : stockCategoryVo.getStockType());
        stockEntity.setStockKind(null == stockCategoryVo.getStockKind() ?
                StockConst.SK_UNKNOWN : stockCategoryVo.getStockKind());
        stockEntity.setDealDate(
                null == sinaRealtimeDealInfoPo.getDt() ? "1900-01-01" : sinaRealtimeDealInfoPo.getDt()
        );
        stockEntity.setDealStatus(
                null == sinaRealtimeDealInfoPo.getDealStatus() ? "" : sinaRealtimeDealInfoPo.getDealStatus()
        );
        stockEntity.setUnknownInfo(
                null == sinaRealtimeDealInfoPo.getUnknownKeyList()
                        ? "" : sinaRealtimeDealInfoPo.getUnknownKeyList().toString()
        );
        try {
            if (null != stockEntity.getId()) {
                stockMapper.update(stockEntity);
            } else {
                stockMapper.insert(stockEntity);
            }
        } catch (Exception e) {
            logger.error(stockEntity.toString());
            logger.error(e.getMessage());
        }
    }
}
