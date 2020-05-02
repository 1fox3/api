package com.fox.api.service.third.stock.sina.api;

import com.fox.api.entity.dto.http.HttpResponseDto;
import com.fox.api.util.HttpUtil;
import com.fox.api.util.StringUtil;
import com.fox.api.entity.po.third.stock.StockRealtimePo;

import java.io.IOException;
import java.util.*;

/**
 * 实时信息
 * @author lusongsong
 */
public class SinaRealtime extends SinaStockBaseApi {
    /**
     * 接口链接,例：http://hq.sinajs.cn/list=sh603383,sh601519
     */
    private static String apiUrl = "http://hq.sinajs.cn/list=";
    /**
     * 返回样例,最后一个字段解释（00:正常,03:停牌,-2:未上市新股）
     * var hq_str_sh603383="顶点软件,75.300,73.200,74.160,75.300,73.200,74.160,74.170,1441855,106849717.000,3900,74.160,1400,74.150,200,74.120,1900,74.100,1600,74.090,4600,74.170,1500,74.180,1200,74.200,300,74.330,100,74.400,2019-12-24,15:00:00,00,";
     * var hq_str_sh601519="大智慧,7.930,7.860,7.980,8.050,7.860,7.970,7.980,47836338,380201052.000,544700,7.970,181700,7.960,260474,7.950,89698,7.940,108200,7.930,73500,7.980,88100,7.990,539700,8.000,92700,8.010,247800,8.020,2019-12-24,15:00:03,00,";
     */

    /**
     * 获取单只股票的实时数据
     * @param stockCode
     * @return
     */
    public StockRealtimePo getRealtimeData(String stockCode) {
        List<String> stockCodes = new LinkedList<>();
        stockCodes.add(stockCode);
        Map<String, StockRealtimePo> stockRealtimeEntityMap = this.getRealtimeData(stockCodes);
        if (stockRealtimeEntityMap.containsKey(stockCode)) {
            return stockRealtimeEntityMap.get(stockCode);
        }
        return new StockRealtimePo();
    }

    /**
     * 获取批量股票的实时数据
     * @param stockCodes
     * @return
     */
    public Map<String, StockRealtimePo> getRealtimeData(List<String> stockCodes) {
        try {
            HttpUtil httpUtil = new HttpUtil();
            httpUtil.setUrl(apiUrl + StringUtil.listToString(stockCodes, ",")).setOriCharset("GBK");
            HttpResponseDto httpResponse = httpUtil.request();
            return this.handleResponse(httpResponse.getContent());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        HashMap<String, StockRealtimePo> hashMap = new HashMap<>();
        return hashMap;
    }

    /**
     * 处理接口返回
     * @param response
     * @return
     */
    private Map<String, StockRealtimePo> handleResponse(String response) {
        HashMap<String, StockRealtimePo> hashMap = new HashMap<>();
        if (response.contains(";")) {
            String[] responseArr = response.trim().split(";");
            for (int i = 0; i< responseArr.length; i++) {
                if (!responseArr[i].equals("")) {
                    String key = this.getStockCode(responseArr[i]);
                    StockRealtimePo stockRealtimeEntity = this.getStockRealtimeEntity(responseArr[i]);
                    if (!key.equals("")) {
                        hashMap.put(key, stockRealtimeEntity);
                    }
                }
            }
        }
        return hashMap;
    }

    /**
     * 获取返回中的股票编号
     * @param response
     * @return
     */
    private static String getStockCode(String response) {
        response = response.trim();
        int index = response.lastIndexOf("=");
        response = response.substring(0, index);
        response = response.replace("var hq_str_", "");
        return response;
    }

    /**
     * 获取股票数据实体
     * @param response
     * @return
     */
    private static StockRealtimePo getStockRealtimeEntity(String response) {
        StockRealtimePo stockRealtimeEntity = new StockRealtimePo();
        String stockCode = getStockCode(response);
        int startIndex = response.indexOf("\"");
        int endIndex = response.lastIndexOf("\"");
        if (startIndex > 0 && endIndex > 0) {
            response = response.substring(startIndex + 1, endIndex);
            if (response.contains(",")) {
                String[] responseArr = response.split(",");
                if (stockCode.startsWith("hk")) {
                    stockRealtimeEntity = buildHkStockRealtimeEntity(responseArr);
                } else {
                    stockRealtimeEntity = buildCnStockRealtimeEntity(responseArr);
                }
            }

        }
        return stockRealtimeEntity;
    }

    /**
     * 构建中国股票信息
     * @param responseArr
     * @return
     */
    private static StockRealtimePo buildCnStockRealtimeEntity(String[] responseArr) {
        StockRealtimePo stockRealtimeEntity = new StockRealtimePo();
        if (null == responseArr || responseArr.length == 0) {
            return stockRealtimeEntity;
        }
        Map<Float, Map<String, Float>> sellList = new HashMap<>();
        List<Float> sellPriceList = new ArrayList<>();
        Map<Float, Map<String, Float>> buyList = new HashMap<>();
        List<Float> buyPriceList = new ArrayList<>();
        Map<String, Float> temp = new LinkedHashMap<>();
        List<String> unknownList = new LinkedList<>();
        for (int i = 0; i < responseArr.length; i++) {
            if (responseArr[i].equals("")) {
                continue;
            }
            if (0 == i) {
                stockRealtimeEntity.setStockName(responseArr[i]);
            }
            if (1 == i) {
                stockRealtimeEntity.setTodayOpenPrice(Float.valueOf(responseArr[i]));
            }
            if (2 == i) {
                stockRealtimeEntity.setYesterdayClosePrice(Float.valueOf(responseArr[i]));
            }
            if (3 == i) {
                stockRealtimeEntity.setCurrentPrice(Float.valueOf(responseArr[i]));
            }
            if (4 == i) {
                stockRealtimeEntity.setTodayHighestPrice(Float.valueOf(responseArr[i]));
            }
            if (5 == i) {
                stockRealtimeEntity.setTodayLowestPrice(Float.valueOf(responseArr[i]));
            }
            if (6 == i) {
                stockRealtimeEntity.setCompeteBuyPrice(Float.valueOf(responseArr[i]));
            }
            if (7 == i) {
                stockRealtimeEntity.setCompeteSellPrice(Float.valueOf(responseArr[i]));
            }
            if (8 == i) {
                stockRealtimeEntity.setDealNum(Long.valueOf(responseArr[i]));
            }
            if (9 == i) {
                stockRealtimeEntity.setDealMoney(Double.valueOf(responseArr[i]));
            }
            if (10 <= i && 29 >= i) {
                if (0 == i % 2) {
                    temp.put("num", Float.valueOf(responseArr[i]));
                } else {
                    temp.put("price", Float.valueOf(responseArr[i]));
                    if (10 <= i && 19 >= i) {
                        buyList.put(temp.get("price"), temp);
                        buyPriceList.add(temp.get("price"));
                    } else {
                        sellList.put(temp.get("price"), temp);
                        sellPriceList.add(temp.get("price"));
                    }
                    temp = new LinkedHashMap<>();
                }
            }
            if (30 == i) {
                stockRealtimeEntity.setCurrentDate(responseArr[i]);
            }
            if (31 == i) {
                stockRealtimeEntity.setCurrentTime(responseArr[i]);
            }
            if (32 <= i) {
                unknownList.add(responseArr[i]);
            }
        }
        if (sellList.size() > 0) {
            Collections.reverse(sellPriceList);
            List<Map<String, Float>> list = new LinkedList<>();
            for(Float price : sellPriceList) {
                list.add(sellList.get(price));
            }
            stockRealtimeEntity.setSellPriceList(list);
        }
        if (buyList.size() > 0) {
            Collections.sort(buyPriceList);
            List<Map<String, Float>> list = new LinkedList<>();
            for(Float price : buyPriceList) {
                list.add(buyList.get(price));
            }
            stockRealtimeEntity.setBuyPriceList(list);
        }
        if (unknownList.size() > 0) {
            stockRealtimeEntity.setUnknownKeyList(unknownList);
        }
        return stockRealtimeEntity;
    }

    /**
     * 构建香港股票信息
     * @param responseArr
     * @return
     */
    private static StockRealtimePo buildHkStockRealtimeEntity(String[] responseArr) {
        StockRealtimePo stockRealtimeEntity = new StockRealtimePo();
        if (null == responseArr || responseArr.length == 0) {
            return stockRealtimeEntity;
        }
        List<String> unknownList = new LinkedList<>();
        for (int i = 0; i < responseArr.length; i++) {
            if (responseArr[i].equals("")) {
                continue;
            }
            if (0 == i) {
                stockRealtimeEntity.setStockNameEn(responseArr[i]);
            }
            if (1 == i) {
                stockRealtimeEntity.setStockName(responseArr[i]);
            }
            if (2 == i) {
                stockRealtimeEntity.setTodayOpenPrice(Float.valueOf(responseArr[i]));
            }
            if (3 == i) {
                stockRealtimeEntity.setYesterdayClosePrice(Float.valueOf(responseArr[i]));
            }
            if (4 == i) {
                stockRealtimeEntity.setTodayHighestPrice(Float.valueOf(responseArr[i]));
            }
            if (5 == i) {
                stockRealtimeEntity.setTodayLowestPrice(Float.valueOf(responseArr[i]));
            }
            if (6 == i) {
                stockRealtimeEntity.setCurrentPrice(Float.valueOf(responseArr[i]));
            }
            if (7 == i) {
                stockRealtimeEntity.setUptickPrice(Float.valueOf(responseArr[i]));
            }
            if (8 == i) {
                stockRealtimeEntity.setUptickRate(Float.valueOf(responseArr[i]));
            }
            if (9 == i) {
                stockRealtimeEntity.setMinuteLowestPrice(Float.valueOf(responseArr[i]));
            }
            if (10 == i) {
                stockRealtimeEntity.setMinuteHighestPrice(Float.valueOf(responseArr[i]));
            }
            if (11 == i) {
                stockRealtimeEntity.setDealMoney(Double.valueOf(responseArr[i]));
            }
            if (12 == i) {
                stockRealtimeEntity.setDealNum(Long.valueOf(responseArr[i]));
            }
            if (13 <= i && 16 >= i) {
                //i==15或16时，近期的最高，底价，时间范围不定，暂时观察至少是近一年的
                unknownList.add(responseArr[i]);
            }
            if (17 == i) {
                stockRealtimeEntity.setCurrentDate(responseArr[i].replace("/", "-"));
            }
            if (18 == i) {
                stockRealtimeEntity.setCurrentTime(responseArr[i] + ":00");
            }
        }
        if (unknownList.size() > 0) {
            stockRealtimeEntity.setUnknownKeyList(unknownList);
        }
        return stockRealtimeEntity;
    }
}
