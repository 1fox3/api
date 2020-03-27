package com.fox.api.service.third.stock.nets.api;

import com.fox.api.entity.dto.http.HttpResponseDto;
import com.fox.api.util.DateUtil;
import com.fox.api.util.HttpUtil;
import com.fox.api.entity.po.third.stock.StockDayLinePo;
import com.fox.api.entity.po.third.stock.StockDealPo;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.util.*;

public class NetsDayLine extends NetsStockBaseApi {
    //样例链接
    private static String demoUrl = "http://img1.money.126.net/data/" +
            "{stockMarketPY}/{rehabilitationType}/day/history/{year}/{stockCode}.json";
    //复权类型
    private static List<String> rehabilitationTypeList = Arrays.asList(
        "kline", //不复权
        "klinederc" //复权
    );
    private String rehabilitationType = "kline";

    /**
     * 获取线图数据
     * @param netsCodeInfoMap
     * @return
     */
    public StockDayLinePo getDayLine(Map<String, String> netsCodeInfoMap, String startDateStr, String endDateStr) {
        String stockCode = netsCodeInfoMap.containsKey("netsStockCode") ?
                (String)netsCodeInfoMap.get("netsStockCode") : "";
        String rehabilitationType = netsCodeInfoMap.containsKey("rehabilitationType") ?
                (String)netsCodeInfoMap.get("rehabilitationType") : "";
        rehabilitationType = this.rehabilitationTypeList.contains(rehabilitationType) ?
                rehabilitationType : this.rehabilitationType;
        StockDayLinePo stockDayLineEntity = new StockDayLinePo();
        if (stockCode.equals("")) {
            return stockDayLineEntity;
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

        try {
            for (int i = startYear; i <= endYear; i++) {
                String url = this.demoUrl.replace("{stockMarketPY}", netsCodeInfoMap.get("netsStockMarketPY"))
                        .replace("{rehabilitationType}", rehabilitationType)
                        .replace("{year}", String.valueOf(i))
                        .replace("{stockCode}", stockCode);
                HttpUtil httpUtil = new HttpUtil();
                httpUtil.setUrl(url).setOriCharset("GBK");
                HttpResponseDto httpResponse = httpUtil.request();
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
                if (stockDayLineEntity.getStockCode() == null) {
                    stockDayLineEntity = currentStockDayLineEntity;
                } else {
                    List<StockDealPo> allList = stockDayLineEntity.getLineNode();
                    if (null != currentStockDayLineEntity.getLineNode()) {
                        allList.addAll(currentStockDayLineEntity.getLineNode());
                    }
                    stockDayLineEntity.setLineNode(allList);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stockDayLineEntity;
    }

    /**
     * 解析数据返回
     * @param response
     * @return
     */
    private StockDayLinePo handleResponse(String response) {
        StockDayLinePo stockDayLineEntity = new StockDayLinePo();
        try {
            JSONObject responseObj = (JSONObject)JSONObject.fromObject(response);
            if (responseObj.containsKey("symbol")) {
                stockDayLineEntity.setStockCode(responseObj.getString("symbol"));
            }
            if (responseObj.containsKey("name")) {
                stockDayLineEntity.setStockName(responseObj.getString("name"));
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
                        stockDayNodeEntity.setOpenPrice(singleArr.getDouble(1));
                        stockDayNodeEntity.setClosePrice(singleArr.getDouble(2));
                        stockDayNodeEntity.setHighestPrice(singleArr.getDouble(3));
                        stockDayNodeEntity.setLowestPrice(singleArr.getDouble(4));
                        stockDayNodeEntity.setDealNum(singleArr.getLong(5));
                        stockDayNodeEntity.setAmplitude(singleArr.getDouble(6));
                        nodeList.add(stockDayNodeEntity);
                    }
                }
                if (0 < nodeList.size()) {
                    stockDayLineEntity.setLineNode(nodeList);
                }
            }
        } catch (JSONException e) {}
        return stockDayLineEntity;
    }
}
