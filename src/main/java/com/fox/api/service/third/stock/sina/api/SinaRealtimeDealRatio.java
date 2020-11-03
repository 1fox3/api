package com.fox.api.service.third.stock.sina.api;

import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.entity.dto.http.HttpResponseDto;
import com.fox.api.entity.po.third.stock.StockDealNumPo;
import com.fox.api.util.DateUtil;
import com.fox.api.util.FileUtil;
import com.fox.api.util.HttpUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 实时交易价格成交占比
 *
 * @author lusongsong
 * @date 2020/11/3 14:40
 */
@Component
public class SinaRealtimeDealRatio extends SinaStockBaseApi {
    /**
     * 样例链接 https://vip.stock.finance.sina.com.cn/quotes_service/view/cn_price.php?symbol=sh603383
     */
    private static String apiUrl = "https://vip.stock.finance.sina.com.cn/quotes_service/view/cn_price.php";

    /**
     * 获取结果保存路径
     *
     * @param stockEntity
     * @return
     */
    private String getFilePath(StockEntity stockEntity) {
        String filePath = saveFilePath(stockEntity, getClass().getSimpleName());
        if (null == filePath || filePath.isEmpty()) {
            return "";
        }
        String currentDate = DateUtil.getCurrentDate();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(filePath);
        stringBuffer.append("/");
        stringBuffer.append(currentDate.substring(0, 4));
        stringBuffer.append("/");
        stringBuffer.append(currentDate.substring(4).replace("-", ""));
        stringBuffer.append(".txt");
        return stringBuffer.toString().replace("//", "/");
    }

    /**
     * 获取成交占比信息
     *
     * @param stockEntity
     * @return
     */
    public List<StockDealNumPo> getDealRatio(StockEntity stockEntity) {
        List<StockDealNumPo> list = new LinkedList<>();
        try {
            List<StockDealNumPo> stockDealNumPoList = new ArrayList<>();
            String resultStr = "";
            String filePath = getFilePath(stockEntity);
            if (null != filePath && !filePath.isEmpty()) {
                resultStr = FileUtil.read(filePath);
                if (null != resultStr && !resultStr.isEmpty()) {
                    JSONArray jsonArray = JSONArray.fromObject(resultStr);
                    if (null != jsonArray && !jsonArray.isEmpty()) {
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            if (null == jsonObject) {
                                continue;
                            }
                            StockDealNumPo stockDealNumPo = new StockDealNumPo();
                            if (jsonObject.containsKey("price")) {
                                stockDealNumPo.setPrice(new BigDecimal(jsonObject.getDouble("price")).setScale(2, RoundingMode.HALF_UP));
                            }
                            if (jsonObject.containsKey("dealNum")) {
                                stockDealNumPo.setDealNum(jsonObject.getLong("dealNum"));
                            }
                            if (jsonObject.containsKey("ratio")) {
                                stockDealNumPo.setRatio(new BigDecimal(jsonObject.getDouble("ratio")).setScale(2, RoundingMode.HALF_UP));
                            }
                            stockDealNumPoList.add(stockDealNumPo);
                        }
                    }
                }
            }

            if (null == stockDealNumPoList || stockDealNumPoList.isEmpty()) {
                HttpUtil httpUtil = new HttpUtil();
                httpUtil.setUrl(apiUrl).setOriCharset(HttpUtil.CHARSET_GBK).setErrorOriCharset(HttpUtil.CHARSET_UTF8);
                httpUtil.setParam("symbol", SinaStockBaseApi.getSinaStockCode(stockEntity));
                HttpResponseDto httpResponse = httpUtil.request();
                if (SinaStockBaseApi.isForbidden(httpResponse)) {
                    SinaStockBaseApi.handleForbidden();
                    httpResponse = httpUtil.request();
                }
                stockDealNumPoList = this.handleResponse(httpResponse.getContent());
                if (null != stockDealNumPoList && !stockDealNumPoList.isEmpty()
                        && null != filePath && !filePath.isEmpty()) {
                    FileUtil.coverWrite(filePath, JSONArray.fromObject(stockDealNumPoList).toString());
                }
            }
            return stockDealNumPoList;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        return list;
    }

    /**
     * 解析返回的信息
     *
     * @param response
     * @return
     */
    private List<StockDealNumPo> handleResponse(String response) {
        List<StockDealNumPo> list = new LinkedList<>();
        int divStartIndex = response.indexOf("<div id=\"divListTemplate\" class=\"divList\">");
        if (0 <= divStartIndex) {
            response = response.substring(divStartIndex);
        }
        int divEndIndex = response.indexOf("</table");
        if (0 <= divEndIndex) {
            response = response.substring(0, divEndIndex);
        }
        //截取表格内容
        int bodyStartIndex = response.indexOf("<tbody");
        int bodyEndIndex = response.lastIndexOf("</tbody");
        if (bodyStartIndex < 0 || bodyEndIndex < 0) {
            return list;
        }
        response = response.substring(bodyStartIndex, bodyEndIndex);
        //匹配没一行的tb属性
        String patternStr = "<td>(.*)<\\/*?";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(response);
        StockDealNumPo stockDealNumPo = new StockDealNumPo();
        int i = 0;
        while (matcher.find()) {
            i++;
            String e = matcher.group(1);
            //匹配价格
            if (1 == i) {
                stockDealNumPo = new StockDealNumPo();
                stockDealNumPo.setPrice(new BigDecimal(e));
            }
            //匹配成交量
            if (2 == i) {
                stockDealNumPo.setDealNum(Long.valueOf(e));
            }
            //匹配占比
            if (3 == i) {
                //去掉占比的百分号
                e = e.replace("%", "");
                stockDealNumPo.setRatio(new BigDecimal(e));
                list.add(stockDealNumPo);
                i = 0;
            }
        }
        return list;
    }
}
