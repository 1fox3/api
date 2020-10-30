package com.fox.api.service.third.stock.nets.api;

import com.fox.api.entity.dto.http.HttpResponseDto;
import com.fox.api.util.HttpUtil;
import com.fox.api.entity.po.third.stock.StockRealtimeLinePo;
import com.fox.api.entity.po.third.stock.StockRealtimeNodePo;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 获取近4天的分钟粒度交易信息(数据不对，禁止使用)
 *
 * @author lusongsong
 * @date 2020/3/5 18:13
 */
public class NetsFourDaysMinuteLine extends NetsStockBaseApi {
    /**
     * 样例链接 http://img1.money.126.net/data/hs/time/4days/1399001.json
     */
    private static String demoUrl = "http://img1.money.126.net/data/{stockMarketPY}/time/4days/{stockCode}.json";

    /**
     * 获取前4天分钟数据
     *
     * @param netsCodeInfoMap
     * @return
     */
    public List<StockRealtimeLinePo> getFourDaysMinuteLine(Map<String, String> netsCodeInfoMap) {
        List<StockRealtimeLinePo> list = new LinkedList<>();
        if (null == netsCodeInfoMap || !netsCodeInfoMap.containsKey("netsStockMarketPY")
                || !netsCodeInfoMap.containsKey("netsStockCode")) {
            return list;
        }
        String netsStockCode = netsCodeInfoMap.get("netsStockCode");
        netsStockCode = null == netsStockCode ? "" : netsStockCode;
        String netsStockMarketPY = netsCodeInfoMap.get("netsStockMarketPY");
        netsStockMarketPY = null == netsStockMarketPY ? "" : netsStockMarketPY;
        try {
            String url = demoUrl.replace("{stockMarketPY}", netsStockMarketPY)
                    .replace("{stockCode}", netsStockCode);
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
     *
     * @param response
     * @return
     */
    private List<StockRealtimeLinePo> handleResponse(String response) {
        List<StockRealtimeLinePo> list = new LinkedList<>();

        JSONObject responseObject = JSONObject.fromObject(response);
        String stockCode = responseObject.containsKey("symbol") ? responseObject.getString("symbol") : "";
        String stockName = responseObject.containsKey("name") ? responseObject.getString("name") : "";

        if (responseObject.containsKey("data")) {
            JSONArray totalDataArr = (JSONArray) responseObject.get("data");
            int totalDataLen = totalDataArr.size();
            for (int i = 0; i < totalDataLen; i++) {
                JSONObject dataObject = (JSONObject) totalDataArr.get(i);
                StockRealtimeLinePo stockRealtimeLineEntity = new StockRealtimeLinePo();
                stockRealtimeLineEntity.setStockCode(stockCode);
                stockRealtimeLineEntity.setStockName(stockName.replace(" ", ""));
                if (dataObject.containsKey("count")) {
                    stockRealtimeLineEntity.setNodeCount(dataObject.getInt("count"));
                }
                if (dataObject.containsKey("yestclose")) {
                    stockRealtimeLineEntity.setPreClosePrice(new BigDecimal(dataObject.getDouble("yestclose")));
                }
                if (dataObject.containsKey("lastVolume")) {
                    stockRealtimeLineEntity.setDealNum(dataObject.getLong("lastVolume"));
                }
                if (dataObject.containsKey("date")) {
                    stockRealtimeLineEntity.setDt(dataObject.getString("lastVolume"));
                }
                if (dataObject.containsKey("data")) {
                    JSONArray dataArr = (JSONArray) dataObject.get("data");
                    List<StockRealtimeNodePo> nodeList = new LinkedList();
                    int dataLen = dataArr.size();
                    for (int j = 0; j < dataLen; j++) {
                        JSONArray noteArr = (JSONArray) dataArr.get(j);
                        if (4 == noteArr.size()) {
                            StockRealtimeNodePo stockRealtimeNodeEntity = new StockRealtimeNodePo();
                            stockRealtimeNodeEntity.setTime(noteArr.getString(0));
                            stockRealtimeNodeEntity.setPrice(new BigDecimal(noteArr.getDouble(1)));
                            stockRealtimeNodeEntity.setAvgPrice(new BigDecimal(noteArr.getDouble(2)));
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
