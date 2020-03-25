package com.fox.api.service.third.stock.nets.api;

import com.fox.api.entity.dto.http.HttpResponseDto;
import com.fox.api.util.HttpUtil;
import com.fox.api.service.third.stock.entity.StockRealtimeLineEntity;
import com.fox.api.service.third.stock.entity.StockRealtimeNodeEntity;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class NetsFourDaysMinuteLine extends NetsStockBaseApi {
    //样例链接
    private static String demoUrl = "http://img1.money.126.net/data/{stockMarketPY}/time/4days/{stockCode}.json";
    private String stockMarketPY = "hs";

    /**
     * 获取前4天分钟数据
     * @param stockCode
     * @param stockMarket
     * @return
     */
    public List<StockRealtimeLineEntity> getFourDaysMinuteLine(String stockCode, String stockMarket) {
        this.stockMarketPY = stockMarket;
        return this.getFourDaysMinuteLine(stockCode);
    }

    /**
     * 获取分钟数据
     * @param stockCode
     * @return
     */
    public List<StockRealtimeLineEntity> getFourDaysMinuteLine(String stockCode) {
        List<StockRealtimeLineEntity> list = new LinkedList<>();
        try {
            String url = this.demoUrl.replace("{stockMarketPY}", this.stockMarketPY)
                    .replace("{stockCode}", stockCode);
            HttpUtil httpUtil = new HttpUtil();
            httpUtil.setUrl(url).setOriCharset("GBK");
            HttpResponseDto httpResponse = httpUtil.request();
            return this.handleResponse(httpResponse.getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 处理返回数据
     * @param response
     * @return
     */
    private List<StockRealtimeLineEntity> handleResponse(String response) {
        List<StockRealtimeLineEntity> list = new LinkedList<>();

        JSONObject responseObject = JSONObject.fromObject(response);
        String stockCode = responseObject.containsKey("symbol") ? responseObject.getString("symbol") : "";
        String stockName = responseObject.containsKey("name") ? responseObject.getString("name") : "";

        if (responseObject.containsKey("data")) {
            JSONArray totalDataArr = (JSONArray)responseObject.get("data");
            int totalDataLen = totalDataArr.size();
            for (int i = 0; i < totalDataLen; i++) {
                JSONObject dataObject = (JSONObject) totalDataArr.get(i);
                StockRealtimeLineEntity stockRealtimeLineEntity = new StockRealtimeLineEntity();
                stockRealtimeLineEntity.setStockCode(stockCode);
                stockRealtimeLineEntity.setStockName(stockName);
                if (dataObject.containsKey("count")) {
                    stockRealtimeLineEntity.setNodeCount(dataObject.getInt("count"));
                }
                if (dataObject.containsKey("yestclose")) {
                    stockRealtimeLineEntity.setYesterdayClosePrice(dataObject.getDouble("yestclose"));
                }
                if (dataObject.containsKey("lastVolume")) {
                    stockRealtimeLineEntity.setDealNum(dataObject.getLong("lastVolume"));
                }
                if (dataObject.containsKey("date")) {
                    stockRealtimeLineEntity.setDate(dataObject.getString("lastVolume"));
                }
                if (dataObject.containsKey("data")) {
                    JSONArray dataArr = (JSONArray)dataObject.get("data");
                    List<StockRealtimeNodeEntity> nodeList = new LinkedList();
                    int dataLen = dataArr.size();
                    for (int j = 0; j < dataLen; j++) {
                        JSONArray noteArr = (JSONArray)dataArr.get(j);
                        if (4 == noteArr.size()) {
                            StockRealtimeNodeEntity stockRealtimeNodeEntity = new StockRealtimeNodeEntity();
                            stockRealtimeNodeEntity.setTime(noteArr.getString(0));
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
                list.add(stockRealtimeLineEntity);
            }
        }
        return list;
    }
}
