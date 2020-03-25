package com.fox.api.service.third.stock.nets.api;

import com.fox.api.entity.dto.http.HttpResponseDto;
import com.fox.api.util.HttpUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * 获取不同日期类型的数据
 */
public class NetsTotalLine extends NetsStockBaseApi {
    //样例链接
    private static String demoUrl = "http://img1.money.126.net/data/" +
            "{stockMarketPY}/{rehabilitationType}/{dateType}/times/{stockCode}.json";
    //复权类型
    private static List<String> rehabilitationTypeList = Arrays.asList(
        "kline", //不复权
        "klinederc" //复权
    );
    //日期类型
    private static List<String> dateTypeList = Arrays.asList(
            "day", //天
            "week", //周
            "month" //月
    );
    private String stockMarketPY = "hs";
    private String rehabilitationType = "kline";

    /**
     * 获取线图数据
     * @param params
     * @return
     */
    public Map<String, Object> getTotalLine(Map<String, String> params) {
        String stockCode = params.containsKey("stockCode") ? params.get("stockCode") : "";
        String dateType = params.containsKey("dateType") ? params.get("dateType") : "day";
        if (!this.dateTypeList.contains(dateType)) {
            dateType = "day";
        }
        Map<String, Object> map = new HashMap<>();
        if (stockCode.equals("")) {
            return map;
        }
        try {
            String url = this.demoUrl.replace("{stockMarketPY}", this.stockMarketPY)
                    .replace("{rehabilitationType}", this.rehabilitationType)
                    .replace("{dateType}", dateType)
                    .replace("{stockCode}", stockCode);
            HttpUtil httpUtil = new HttpUtil();
            httpUtil.setUrl(url).setOriCharset("GBK");
            HttpResponseDto httpResponse = httpUtil.request();
            return this.handleResponse(httpResponse.getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 解析数据返回
     * @param response
     * @return
     */
    private Map<String, Object> handleResponse(String response) {
        Map<String, Object> map = new HashMap<>();
        JSONObject responseObj = (JSONObject)JSONObject.fromObject(response);
        if (responseObj.containsKey("symbol")) {
            map.put("stockCode", responseObj.getString("symbol"));
        }
        if (responseObj.containsKey("name")) {
            map.put("stockName", responseObj.getString("name"));
        }
        JSONArray closePriceArr = (JSONArray)responseObj.get("closes");
        JSONArray dateArr = (JSONArray)responseObj.get("times");
        int dateLen = dateArr.size();
        int closePriceLen = closePriceArr.size();
        Map<String, Double> dataMap = new TreeMap<>();
        if (dateLen == closePriceLen) {
            for (int i = 0; i < dateLen; i++) {
                dataMap.put(dateArr.getString(i), closePriceArr.getDouble(i));
            }
        }
        if (!dataMap.isEmpty()) {
            map.put("lineNode", dataMap);
        }

        return map;
    }
}
