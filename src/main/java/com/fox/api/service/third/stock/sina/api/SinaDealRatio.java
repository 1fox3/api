package com.fox.api.service.third.stock.sina.api;

import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.entity.dto.http.HttpResponseDto;
import com.fox.api.util.HttpUtil;
import com.fox.api.entity.po.third.stock.StockDealNumPo;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 获取成交占比信息
 *
 * @author lusongsong
 * @date 2020/3/5 18:13
 */
@Component
public class SinaDealRatio extends SinaStockBaseApi {
    /**
     * 样例链接 http://market.finance.sina.com.cn/pricehis.php?symbol=sh603383&startdate=2019-12-01&enddate=2019-12-13
     */
    private static String apiUrl = "http://market.finance.sina.com.cn/pricehis.php";

    /**
     * 获取成交占比信息
     *
     * @param stockEntity
     * @param startDate
     * @param endDate
     * @return
     */
    public List<StockDealNumPo> getDealRatio(StockEntity stockEntity, String startDate, String endDate) {
        List<StockDealNumPo> list = new LinkedList<>();
        try {
            HttpUtil httpUtil = new HttpUtil();
            httpUtil.setUrl(apiUrl).setOriCharset("GBK");
            httpUtil.setParam("symbol", SinaStockBaseApi.getSinaStockCode(stockEntity));
            httpUtil.setParam("startdate", startDate);
            httpUtil.setParam("enddate", endDate);
            HttpResponseDto httpResponse = httpUtil.request();
            return this.handleResponse(httpResponse.getContent());
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
        //截取表格内容
        int bodyStartIndex = response.indexOf("<tbody");
        int bodyEndIndex = response.lastIndexOf("</tbody");
        response = response.substring(bodyStartIndex, bodyEndIndex);
        //匹配没一行的tb属性
        String patternStr = "<td>(.*)<\\/td>*?";
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
