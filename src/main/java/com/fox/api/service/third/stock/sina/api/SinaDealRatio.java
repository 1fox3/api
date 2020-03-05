package com.fox.api.service.third.stock.sina.api;

import com.fox.api.common.entity.HttpResponse;
import com.fox.api.common.util.HttpUtil;
import com.fox.api.service.third.stock.entity.StockDealNumEntity;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 获取成交占比信息
 */
public class SinaDealRatio extends SinaStockBaseApi {
    //接口
    private static String apiUrl = "http://market.finance.sina.com.cn/pricehis.php";

    /**
     * 获取成交占比信息
     * @param stockCode
     * @param startDate
     * @param endDate
     * @return
     */
    public List<StockDealNumEntity> getDealRatio(String stockCode, String startDate, String endDate) {
        List<StockDealNumEntity> list = new LinkedList<>();
        try {
            HttpUtil httpUtil = new HttpUtil();
            httpUtil.setUrl(apiUrl).setOriCharset("GBK");
            httpUtil.setParam("symbol", stockCode);
            httpUtil.setParam("startdate", startDate);
            httpUtil.setParam("enddate", endDate);
            HttpResponse httpResponse = httpUtil.request();
            return this.handleResponse(httpResponse.getContent());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        return list;
    }

    /**
     * 解析返回的信息
     * @param response
     * @return
     */
    private List<StockDealNumEntity> handleResponse(String response) {
        List<StockDealNumEntity> list = new LinkedList<>();
        //截取表格内容
        int bodyStartIndex = response.indexOf("<tbody");
        int bodyEndIndex = response.lastIndexOf("</tbody");
        response = response.substring(bodyStartIndex, bodyEndIndex);
        //匹配没一行的tb属性
        String patternStr = "<td>(.*)<\\/td>*?";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(response);
        StockDealNumEntity stockDealNumEntity = new StockDealNumEntity();
        int i = 0;
        while (matcher.find()) {
            i++;
            String e = matcher.group(1);
            if (1 == i) {//匹配价格
                stockDealNumEntity = new StockDealNumEntity();
                stockDealNumEntity.setPrice(Float.valueOf(e));
            }
            if (2 == i) {//匹配成交量
                stockDealNumEntity.setDealNum(Long.valueOf(e));
            }
            if (3 == i) {//匹配占比
                //去掉占比的百分号
                e = e.replace("%", "");
                stockDealNumEntity.setRatio(Float.valueOf(e));
                list.add(stockDealNumEntity);
                i = 0;
            }
        }
        return list;
    }
}
