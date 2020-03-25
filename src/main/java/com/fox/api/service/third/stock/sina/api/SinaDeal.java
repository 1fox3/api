package com.fox.api.service.third.stock.sina.api;

import com.fox.api.entity.dto.http.HttpResponseDto;
import com.fox.api.util.HttpUtil;
import com.fox.api.service.third.stock.entity.StockDealEntity;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 获取成交信息
 */
public class SinaDeal extends SinaStockBaseApi {
    //接口
    private static String apiUrl = "http://money.finance.sina.com.cn/quotes_service/api/json_v2.php/CN_MarketData.getKLineData";
    //支持的时间粒度
    private static List<Integer> scaleList = Arrays.asList(5, 15, 30, 60, 240, 1200, 1680, 86400);

    /**
     * 获取交易信息列表
     * @param stockCode
     * @param scale
     * @param dataLen
     * @return
     */
    public List<StockDealEntity> getDealList(String stockCode, Integer scale, Integer dataLen) {
        List<StockDealEntity> list = new LinkedList<>();
        try {
            if (!this.scaleList.contains(scale)) {
                return list;
            }
            HttpUtil httpUtil = new HttpUtil();
            httpUtil.setUrl(apiUrl).setOriCharset("GBK");
            httpUtil.setParam("symbol", stockCode);
            httpUtil.setParam("scale", Integer.toString(scale));
            httpUtil.setParam("datalen", Integer.toString(dataLen));
            httpUtil.setParam("ma", "no");
            HttpResponseDto httpResponse = httpUtil.request();
            return this.handleResponse(httpResponse.getContent());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        return list;
    }

    /**
     * 处理接口返回
     * @param response
     * @return
     */
    public List<StockDealEntity> handleResponse(String response) {
        //给json字符串的key加双引号
        response = this.handleJsonStr(response);
        List<StockDealEntity> list = new LinkedList<>();
        try {
            JSONArray jsonArray = JSONArray.fromObject (response); //其中的这个data是接口传来的json数据
            int arrayLen = jsonArray.size();
            for (int i = 0; i < arrayLen; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                StockDealEntity stockDealEntity = new StockDealEntity();
                stockDealEntity.setDateTime(jsonObject.get("day").toString());
                stockDealEntity.setOpenPrice(jsonObject.getDouble("open"));
                stockDealEntity.setHighestPrice(jsonObject.getDouble("high"));
                stockDealEntity.setLowestPrice(jsonObject.getDouble("low"));
                stockDealEntity.setClosePrice(jsonObject.getDouble("close"));
                stockDealEntity.setDealNum(jsonObject.getLong("volume"));
                list.add(stockDealEntity);
            }
        } catch (JSONException e) {
        }
        return list;
    }
}
