package com.fox.api.service.third.stock.nets.api;

import com.fox.api.entity.dto.http.HttpResponseDto;
import com.fox.api.util.DateUtil;
import com.fox.api.util.HttpUtil;
import com.fox.api.entity.po.third.stock.StockDayLinePo;
import com.fox.api.entity.po.third.stock.StockDealPo;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 股票按天成交信息
 * @author lusongsong
 * @date 2020/3/5 18:13
 */
public class NetsDayLine extends NetsStockBaseApi {
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 样例链接 http://img1.money.126.net/data/hs/kline/day/history/2020/0603383.json
     */
    private static String demoUrl = "http://img1.money.126.net/data/" +
            "{stockMarketPY}/{rehabilitationType}/day/history/{year}/{stockCode}.json";
    /**
     * 复权类型
     */
    private static List<String> rehabilitationTypeList = Arrays.asList(
            //不复权
            "kline",
            //复权
            "klinederc"
    );
    /**
     * 默认复权类型
     */
    private String rehabilitationType = "kline";

    /**
     * 获取线图数据
     * @param netsCodeInfoMap
     * @return
     */
    public StockDayLinePo getDayLine(Map<String, String> netsCodeInfoMap, String startDateStr, String endDateStr) {
        String stockCode = netsCodeInfoMap.containsKey("netsStockCode") ?
                netsCodeInfoMap.get("netsStockCode") : "";
        String rehabilitationType = netsCodeInfoMap.containsKey("rehabilitationType") ?
                netsCodeInfoMap.get("rehabilitationType") : "";
        rehabilitationType = rehabilitationTypeList.contains(rehabilitationType) ?
                rehabilitationType : this.rehabilitationType;
        StockDayLinePo stockDayLinePo = new StockDayLinePo();
        if (stockCode.equals("")) {
            return stockDayLinePo;
        }

        Date startDate = DateUtil.getDateFromStr(startDateStr);
        Date endDate = DateUtil.getDateFromStr(endDateStr);
        if (startDate.compareTo(endDate) == 1) {
            Date tempDate = startDate;
            startDate = endDate;
            endDate = tempDate;
        }

        int startYear = Integer.valueOf(
                DateUtil.dateStrFormatChange(
                        DateUtil.dateToStr(startDate, DateUtil.DATE_FORMAT_1),
                        DateUtil.DATE_FORMAT_1,
                        DateUtil.YEAR_FORMAT_1
                )
        );
        int endYear = Integer.valueOf(
                DateUtil.dateStrFormatChange(
                        DateUtil.dateToStr(endDate, DateUtil.DATE_FORMAT_1),
                        DateUtil.DATE_FORMAT_1,
                        DateUtil.YEAR_FORMAT_1
                )
        );

        String url = "";
        HttpResponseDto httpResponse;
        try {
            for (int i = startYear; i <= endYear; i++) {
                url = demoUrl.replace("{stockMarketPY}", netsCodeInfoMap.get("netsStockMarketPY"))
                        .replace("{rehabilitationType}", rehabilitationType)
                        .replace("{year}", String.valueOf(i))
                        .replace("{stockCode}", stockCode);
                HttpUtil httpUtil = new HttpUtil();
                httpUtil.setUrl(url).setOriCharset("GBK");
                httpResponse = httpUtil.request();
                StockDayLinePo currentStockDayLineEntity = this.handleResponse(httpResponse.getContent());
                List<StockDealPo> list = currentStockDayLineEntity.getLineNode();
                List<StockDealPo> filterList = new LinkedList<>();
                if (null != list && list.size() > 0) {
                    for (StockDealPo stockDayNodeEntity : list) {
                        Date currentDate = DateUtil.getDateFromStr(stockDayNodeEntity.getDateTime());
                        if (startDate.compareTo(currentDate) <=0 && currentDate.compareTo(endDate) <= 0) {
                            filterList.add(stockDayNodeEntity);
                        }
                    }
                    currentStockDayLineEntity.setLineNode(filterList);
                }
                if (null == stockDayLinePo.getStockCode()) {
                    stockDayLinePo = currentStockDayLineEntity;
                } else {
                    List<StockDealPo> allList = stockDayLinePo.getLineNode();
                    if (null != currentStockDayLineEntity.getLineNode()) {
                        allList.addAll(currentStockDayLineEntity.getLineNode());
                    }
                    stockDayLinePo.setLineNode(allList);
                }
            }
        } catch (IOException e) {
            logger.error(url);
            logger.error(e.getMessage());
        }
        return stockDayLinePo;
    }

    /**
     * 解析数据返回
     * @param response
     * @return
     */
    private StockDayLinePo handleResponse(String response) {
        StockDayLinePo stockDayLinePo = new StockDayLinePo();
        if (null == response || response.isEmpty()) {
            return stockDayLinePo;
        }
        try {
            JSONObject responseObj = (JSONObject)JSONObject.fromObject(response);
            if (responseObj.containsKey("symbol")) {
                stockDayLinePo.setStockCode(responseObj.getString("symbol"));
            }
            if (responseObj.containsKey("name")) {
                stockDayLinePo.setStockName(responseObj.getString("name"));
            }
            if (responseObj.containsKey("data")) {
                JSONArray dataArr = (JSONArray)responseObj.get("data");
                int dataLen = dataArr.size();
                List<StockDealPo> nodeList = new LinkedList();
                for (int i = 0; i < dataLen; i++) {
                    JSONArray singleArr = (JSONArray)dataArr.get(i);
                    if (7 == singleArr.size()) {
                        StockDealPo stockDayNodeEntity = new StockDealPo();
                        stockDayNodeEntity.setDateTime(
                                DateUtil.dateStrFormatChange(
                                        singleArr.getString(0), DateUtil.DATE_FORMAT_2, DateUtil.DATE_FORMAT_1
                                )
                        );
                        stockDayNodeEntity.setOpenPrice(new BigDecimal(singleArr.getDouble(1)));
                        stockDayNodeEntity.setClosePrice(new BigDecimal(singleArr.getDouble(2)));
                        stockDayNodeEntity.setHighestPrice(new BigDecimal(singleArr.getDouble(3)));
                        stockDayNodeEntity.setLowestPrice(new BigDecimal(singleArr.getDouble(4)));
                        stockDayNodeEntity.setDealNum(singleArr.getLong(5));
                        stockDayNodeEntity.setUptickRate(new BigDecimal(singleArr.getDouble(6)));
                        nodeList.add(stockDayNodeEntity);
                    }
                }
                if (0 < nodeList.size()) {
                    stockDayLinePo.setLineNode(nodeList);
                }
            }
        } catch (JSONException e) {
            logger.error(response);
            logger.error(e.getMessage());
        }
        return stockDayLinePo;
    }
}
