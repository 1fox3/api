package com.fox.api.service.third.stock.sina.api;

import com.fox.api.entity.dto.http.HttpResponseDto;
import com.fox.api.util.HttpUtil;
import com.fox.api.util.StringUtil;
import com.fox.api.entity.po.third.stock.StockRealtimePo;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 实时信息
 * @author lusongsong
 */
@Component
public class SinaRealtime extends SinaStockBaseApi {
    /**
     * 接口链接,例：http://hq.sinajs.cn/list=sh603383,sh601519
     */
    private static String apiUrl = "http://hq.sinajs.cn/list=";
    /**
     * 返回样例,最后一个字段解释（00:正常,03:停牌,07:临时停牌,-2:未上市新股）
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
                    String key = getStockCode(responseArr[i]);
                    StockRealtimePo stockRealtimePo = getStockRealtimeEntity(responseArr[i]);
                    if (!key.equals("")) {
                        hashMap.put(key, stockRealtimePo);
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
        StockRealtimePo stockRealtimePo = new StockRealtimePo();
        String stockCode = getStockCode(response);
        int startIndex = response.indexOf("\"");
        int endIndex = response.lastIndexOf("\"");
        if (startIndex > 0 && endIndex > 0) {
            response = response.substring(startIndex + 1, endIndex);
            if (response.contains(",")) {
                String[] responseArr = response.split(",");
                if (stockCode.startsWith("hk")) {
                    stockRealtimePo = buildHkStockRealtimeEntity(responseArr);
                } else {
                    stockRealtimePo = buildCnStockRealtimeEntity(responseArr);
                }
            }

        }
        return stockRealtimePo;
    }

    /**
     * 构建中国股票信息
     * @param responseArr
     * @return
     */
    private static StockRealtimePo buildCnStockRealtimeEntity(String[] responseArr) {
        StockRealtimePo stockRealtimePo = new StockRealtimePo();
        if (null == responseArr || responseArr.length == 0) {
            return stockRealtimePo;
        }
        Map<BigDecimal, Map<String, BigDecimal>> sellList = new LinkedHashMap<>(5);
        List<BigDecimal> sellPriceList = new ArrayList<>();
        Map<BigDecimal, Map<String, BigDecimal>> buyList = new LinkedHashMap<>(5);
        List<BigDecimal> buyPriceList = new ArrayList<>();
        Map<String, BigDecimal> temp = new LinkedHashMap<>();
        List<String> unknownList = new LinkedList<>();
        for (int i = 0; i < responseArr.length; i++) {
            if (responseArr[i].equals("")) {
                continue;
            }
            if (0 == i) {
                stockRealtimePo.setStockName(responseArr[i]);
            }
            if (1 == i) {
                stockRealtimePo.setOpenPrice(new BigDecimal(responseArr[i]));
            }
            if (2 == i) {
                stockRealtimePo.setPreClosePrice(new BigDecimal(responseArr[i]));
            }
            if (3 == i) {
                stockRealtimePo.setCurrentPrice(new BigDecimal(responseArr[i]));
            }
            if (4 == i) {
                stockRealtimePo.setHighestPrice(new BigDecimal(responseArr[i]));
            }
            if (5 == i) {
                stockRealtimePo.setLowestPrice(new BigDecimal(responseArr[i]));
            }
            if (6 == i) {
                stockRealtimePo.setCompeteBuyPrice(new BigDecimal(responseArr[i]));
            }
            if (7 == i) {
                stockRealtimePo.setCompeteSellPrice(new BigDecimal(responseArr[i]));
            }
            if (8 == i) {
                stockRealtimePo.setDealNum(Long.valueOf(responseArr[i]));
            }
            if (9 == i) {
                stockRealtimePo.setDealMoney(new BigDecimal(responseArr[i]));
            }
            if (10 <= i && 29 >= i) {
                if (0 == i % 2) {
                    temp.put("num", new BigDecimal(responseArr[i]));
                } else {
                    temp.put("price", new BigDecimal(responseArr[i]));
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
                stockRealtimePo.setCurrentDate(responseArr[i]);
            }
            if (31 == i) {
                stockRealtimePo.setCurrentTime(responseArr[i]);
            }
            if (32 == i) {
                stockRealtimePo.setDealStatus(responseArr[i]);
            }
            if (33 <= i) {
                unknownList.add(responseArr[i]);
            }
        }
        if (sellList.size() > 0) {
            Collections.reverse(sellPriceList);
            List<Map<String, BigDecimal>> list = new LinkedList<>();
            for(BigDecimal price : sellPriceList) {
                list.add(sellList.get(price));
            }
            stockRealtimePo.setSellPriceList(list);
        }
        if (buyList.size() > 0) {
            Collections.sort(buyPriceList);
            List<Map<String, BigDecimal>> list = new LinkedList<>();
            for(BigDecimal price : buyPriceList) {
                list.add(0, buyList.get(price));
            }
            stockRealtimePo.setBuyPriceList(list);
        }
        if (unknownList.size() > 0) {
            stockRealtimePo.setUnknownKeyList(unknownList);
        }
        //昨日收盘价
        BigDecimal preClosePrice = stockRealtimePo.getPreClosePrice();
        //当前价格
        BigDecimal currentPrice = stockRealtimePo.getCurrentPrice();
        //今日最高价
        BigDecimal highestPrice = stockRealtimePo.getHighestPrice();
        //今日最低价
        BigDecimal lowestPrice = stockRealtimePo.getLowestPrice();
        if (null == currentPrice || null == preClosePrice
                || null == highestPrice || null == lowestPrice
                || 0 == preClosePrice.compareTo(BigDecimal.ZERO)
        ) {
            return stockRealtimePo;
        }
        BigDecimal uptickPrice = currentPrice.subtract(preClosePrice);
        stockRealtimePo.setUptickPrice(uptickPrice);
        //增幅
        BigDecimal uptickRate = uptickPrice.divide(preClosePrice);
        //波动
        BigDecimal surgeRate = highestPrice.subtract(lowestPrice).divide(preClosePrice);
        stockRealtimePo.setUptickRate(uptickRate);
        stockRealtimePo.setSurgeRate(surgeRate);
        return stockRealtimePo;
    }

    /**
     * 构建香港股票信息
     * @param responseArr
     * @return
     */
    private static StockRealtimePo buildHkStockRealtimeEntity(String[] responseArr) {
        StockRealtimePo stockRealtimePo = new StockRealtimePo();
        if (null == responseArr || responseArr.length == 0) {
            return stockRealtimePo;
        }
        List<String> unknownList = new LinkedList<>();
        for (int i = 0; i < responseArr.length; i++) {
            if (responseArr[i].equals("")) {
                continue;
            }
            if (0 == i) {
                stockRealtimePo.setStockNameEn(responseArr[i]);
            }
            if (1 == i) {
                stockRealtimePo.setStockName(responseArr[i]);
            }
            if (2 == i) {
                stockRealtimePo.setOpenPrice(new BigDecimal(responseArr[i]));
            }
            if (3 == i) {
                stockRealtimePo.setPreClosePrice(new BigDecimal(responseArr[i]));
            }
            if (4 == i) {
                stockRealtimePo.setHighestPrice(new BigDecimal(responseArr[i]));
            }
            if (5 == i) {
                stockRealtimePo.setLowestPrice(new BigDecimal(responseArr[i]));
            }
            if (6 == i) {
                stockRealtimePo.setCurrentPrice(new BigDecimal(responseArr[i]));
            }
            if (7 == i) {
                stockRealtimePo.setUptickPrice(new BigDecimal(responseArr[i]));
            }
            if (8 == i) {
                stockRealtimePo.setUptickRate(new BigDecimal(responseArr[i]));
            }
            if (9 == i) {
                stockRealtimePo.setMinuteLowestPrice(new BigDecimal(responseArr[i]));
            }
            if (10 == i) {
                stockRealtimePo.setMinuteHighestPrice(new BigDecimal(responseArr[i]));
            }
            if (11 == i) {
                stockRealtimePo.setDealMoney(new BigDecimal(responseArr[i]));
            }
            if (12 == i) {
                stockRealtimePo.setDealNum(Long.valueOf(responseArr[i]));
            }
            if (13 <= i && 16 >= i) {
                //i==15或16时，近期的最高，底价，时间范围不定，暂时观察至少是近一年的
                unknownList.add(responseArr[i]);
            }
            if (17 == i) {
                stockRealtimePo.setCurrentDate(responseArr[i].replace("/", "-"));
            }
            if (18 == i) {
                stockRealtimePo.setCurrentTime(responseArr[i] + ":00");
            }
        }
        if (unknownList.size() > 0) {
            stockRealtimePo.setUnknownKeyList(unknownList);
        }
        //上个交易日收盘价
        BigDecimal preClosePrice = stockRealtimePo.getPreClosePrice();
        //当前价格
        BigDecimal currentPrice = stockRealtimePo.getCurrentPrice();
        //最高价
        BigDecimal highestPrice = stockRealtimePo.getHighestPrice();
        //最低价
        BigDecimal lowestPrice = stockRealtimePo.getLowestPrice();
        if (null == currentPrice || null == preClosePrice
                || null == highestPrice || null == lowestPrice
                || 0 == preClosePrice.compareTo(BigDecimal.ZERO)
        ) {
            return stockRealtimePo;
        }
        BigDecimal uptickPrice = currentPrice.subtract(preClosePrice);
        stockRealtimePo.setUptickPrice(uptickPrice);
        //增幅
        BigDecimal uptickRate = uptickPrice.divide(preClosePrice);
        //波动
        BigDecimal surgeRate = highestPrice.subtract(lowestPrice).divide(preClosePrice);
        stockRealtimePo.setUptickRate(uptickRate);
        stockRealtimePo.setSurgeRate(surgeRate);
        return stockRealtimePo;
    }
}
