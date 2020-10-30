package com.fox.api.service.third.stock.nets.api;

import com.fox.api.entity.dto.http.HttpResponseDto;
import com.fox.api.util.HttpUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 获取不同日期类型的所有时间节点的收盘价数据
 *
 * @author lusongsong
 * @date 2020/3/5 18:13
 */
public class NetsTotalCloseLine extends NetsStockBaseApi {
    /**
     * 样例链接 http://img1.money.126.net/data/hs/kline/day/times/1399001.json
     */
    private static String demoUrl = "http://img1.money.126.net/data/" +
            "{stockMarketPY}/{rehabilitationType}/{dateType}/times/{stockCode}.json";
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
     * 日期类型
     */
    private static List<String> dateTypeList = Arrays.asList(
            "day",
            "week",
            "month"
    );

    /**
     * 获取线图数据
     *
     * @param netsCodeInfoMap
     * @return
     */
    public Map<String, Object> getTotalLine(Map<String, String> netsCodeInfoMap) {
        String netsStockMarketPY = netsCodeInfoMap.containsKey("netsStockMarketPY") ?
                netsCodeInfoMap.get("netsStockMarketPY") : "";
        String netsStockCode = netsCodeInfoMap.containsKey("netsStockCode") ?
                netsCodeInfoMap.get("netsStockCode") : "";
        String rehabilitationType = netsCodeInfoMap.containsKey("rehabilitationType") ?
                netsCodeInfoMap.get("rehabilitationType") : "";
        rehabilitationType = rehabilitationTypeList.contains(rehabilitationType) ?
                rehabilitationType : rehabilitationTypeList.get(0);
        String dateType = netsCodeInfoMap.containsKey("dateType") ?
                netsCodeInfoMap.get("dateType") : "";

        if (!dateTypeList.contains(dateType)) {
            dateType = "day";
        }
        Map<String, Object> map = new HashMap<>(0);
        if (netsStockCode.equals("")) {
            return map;
        }
        try {
            String url = demoUrl.replace("{stockMarketPY}", netsStockMarketPY)
                    .replace("{rehabilitationType}", rehabilitationType)
                    .replace("{dateType}", dateType)
                    .replace("{stockCode}", netsStockCode);
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
     *
     * @param response
     * @return
     */
    private Map<String, Object> handleResponse(String response) {
        Map<String, Object> map = new HashMap<>(3);
        JSONObject responseObj = (JSONObject) JSONObject.fromObject(response);
        if (responseObj.containsKey("symbol")) {
            map.put("stockCode", responseObj.getString("symbol"));
        }
        if (responseObj.containsKey("name")) {
            map.put("stockName", responseObj.getString("name"));
        }
        JSONArray closePriceArr = (JSONArray) responseObj.get("closes");
        JSONArray dateArr = (JSONArray) responseObj.get("times");
        int dateLen = dateArr.size();
        int closePriceLen = closePriceArr.size();
        Map<String, BigDecimal> dataMap = new TreeMap<>();
        if (dateLen == closePriceLen) {
            for (int i = 0; i < dateLen; i++) {
                dataMap.put(dateArr.getString(i), new BigDecimal(closePriceArr.getDouble(i)));
            }
        }
        if (!dataMap.isEmpty()) {
            map.put("lineNode", dataMap);
        }

        return map;
    }
}
