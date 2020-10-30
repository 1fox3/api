package com.fox.api.service.third.stock.sina.api;

import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.entity.dto.http.HttpResponseDto;
import com.fox.api.util.HttpUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 获取复权线图
 *
 * @author lusongsong
 * @date 2020/3/5 18:13
 */
public class SinaRehabilitationLine extends SinaStockBaseApi {
    /**
     * 样例链接 http://finance.sina.com.cn/realstock/company/sh603383/qianfuquan.js
     */
    private static String demoUrl = "http://finance.sina.com.cn/realstock/company/{stockCode}/{rehabilitationType}.js";
    /**
     * 复权类型
     */
    private static List<String> rehabilitationTypeList = Arrays.asList("qianfuquan", "houfuquan");

    /**
     * 获取复权信息
     *
     * @param stockEntity
     * @param rehabilitationType
     * @return
     */
    public Map<String, BigDecimal> getRehabilitationLine(StockEntity stockEntity, String rehabilitationType) {
        Map<String, BigDecimal> map = new HashMap<>();
        try {
            if (!rehabilitationTypeList.contains(rehabilitationType)) {
                return map;
            }
            String url = demoUrl.replace("{stockCode}", SinaStockBaseApi.getSinaStockCode(stockEntity))
                    .replace("{rehabilitationType}", rehabilitationType);
            HttpUtil httpUtil = new HttpUtil();
            httpUtil.setUrl(url).setOriCharset("GBK");
            HttpResponseDto httpResponse = httpUtil.request();
            return this.handleResponse(httpResponse.getContent());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        return map;
    }

    /**
     * 处理返回信息
     *
     * @param response
     * @return
     */
    private Map<String, BigDecimal> handleResponse(String response) {
        //去掉返回数据中的注释信息
        response = this.clearAnnotation(response);
        //给json字符串的key加双引号
        response = this.handleJsonStr(response);
        JSONArray jsonArray = JSONArray.fromObject(response);
        JSONObject jsonObject = (JSONObject) jsonArray.get(0);
        JSONObject dataObject = (JSONObject) jsonObject.get("data");
        Iterator<String> dataKeyIterator = dataObject.keys();
        Map<String, BigDecimal> map = new TreeMap<>();
        while (dataKeyIterator.hasNext()) {
            String key = dataKeyIterator.next();
            BigDecimal value = new BigDecimal(dataObject.getString(key));
            if (key.startsWith("_")) {
                key = key.substring(1);
            }
            map.put(key, value);
        }
        return map;
    }

    /**
     * 去掉返回数据中的注释信息
     *
     * @param response
     * @return
     */
    private String clearAnnotation(String response) {
        int annotationIndex = response.lastIndexOf("/*");
        return response.substring(0, annotationIndex);
    }
}
