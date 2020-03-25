package com.fox.api.service.third.stock.nets.api;

import com.fox.api.entity.dto.http.HttpResponseDto;
import com.fox.api.util.DateUtil;
import com.fox.api.util.HttpUtil;
import com.fox.api.service.third.stock.entity.StockRealtimeLineEntity;
import com.fox.api.service.third.stock.entity.StockRealtimeNodeEntity;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * 实时分钟信息
 */
public class NetsMinuteRealtime extends NetsStockBaseApi {
    //样例链接
    private static String demoUrl = "http://img1.money.126.net/data/{stockMarketPY}/time/today/{stockCode}.json";
    //http://img1.money.126.net/data/hk/time/today/00700.json

    /**
     * 获取分钟数据
     * @param netsCodeInfoMap
     * @return
     */
    public StockRealtimeLineEntity getRealtimeData(Map<String, String> netsCodeInfoMap) {
        try {
            String url = this.demoUrl.replace("{stockMarketPY}", netsCodeInfoMap.get("netsStockMarketPY"))
                    .replace("{stockCode}", netsCodeInfoMap.get("netsStockCode"));
            HttpUtil httpUtil = new HttpUtil();
            httpUtil.setUrl(url).setOriCharset("GBK");
            HttpResponseDto httpResponse = httpUtil.request();
            return this.handleResponse(httpResponse.getContent());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        return new StockRealtimeLineEntity();
    }

    /**
     * 处理返回数据
     * @param response
     * @return
     */
    private StockRealtimeLineEntity handleResponse(String response) {
        StockRealtimeLineEntity stockRealtimeLineEntity = new StockRealtimeLineEntity();
        if (response.equals("")) {
            return stockRealtimeLineEntity;
        }
        JSONObject responseObject = JSONObject.fromObject(response);
        if (responseObject.containsKey("count")) {
            stockRealtimeLineEntity.setNodeCount(responseObject.getInt("count"));
        }
        if (responseObject.containsKey("symbol")) {
            stockRealtimeLineEntity.setStockCode(responseObject.getString("symbol"));
        }
        if (responseObject.containsKey("name")) {
            stockRealtimeLineEntity.setStockName(responseObject.getString("name"));
        }
        if (responseObject.containsKey("yestclose")) {
            stockRealtimeLineEntity.setYesterdayClosePrice(responseObject.getDouble("yestclose"));
        }
        if (responseObject.containsKey("lastVolume")) {
            stockRealtimeLineEntity.setDealNum(responseObject.getLong("lastVolume"));
        }
        if (responseObject.containsKey("date")) {
            stockRealtimeLineEntity.setDate(
                DateUtil.dateStrFormatChange(
                    responseObject.getString("date"),
                    DateUtil.DATE_FORMAT_2,
                    DateUtil.DATE_FORMAT_1
                )
            );
        }
        if (responseObject.containsKey("data")) {
            JSONArray dataArr = (JSONArray)responseObject.get("data");
            List<StockRealtimeNodeEntity> nodeList = new LinkedList();
            int dataLen = dataArr.size();
            for (int i = 0; i < dataLen; i++) {
                JSONArray noteArr = (JSONArray)dataArr.get(i);
                if (4 == noteArr.size()) {
                    StockRealtimeNodeEntity stockRealtimeNodeEntity = new StockRealtimeNodeEntity();
                    String timeStr = noteArr.getString(0);
                    stockRealtimeNodeEntity.setTime(timeStr.substring(0, 2) + ":" + timeStr.substring(2, 4));
                    stockRealtimeNodeEntity.setPrice(noteArr.getDouble(1));
                    stockRealtimeNodeEntity.setAvgPrice(noteArr.getDouble(2));
                    stockRealtimeNodeEntity.setDealNum(noteArr.getLong(3));
                    nodeList.add(stockRealtimeNodeEntity);
                }
            }
            if (nodeList.size() > 0) {
                stockRealtimeLineEntity.setLineNode(nodeList);
            }
        }
        return stockRealtimeLineEntity;
    }

}
