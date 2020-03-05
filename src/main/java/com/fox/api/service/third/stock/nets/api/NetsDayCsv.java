package com.fox.api.service.third.stock.nets.api;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class NetsDayCsv extends NetsStockBaseApi {
    //样例链接
    private static String demoUrl = "http://quotes.money.163.com/service/chddata.html";
    //字段列表
    private static List<String> fieldList = Arrays.asList(
            "TCLOSE", //收盘价
            "HIGH", //最高价
            "LOW", //最低价
            "TOPEN", //开盘价
            "LCLOSE", //前收盘价
            "CHG", //涨跌额
            "PCHG", //涨跌幅
            "TURNOVER", //换手率
            "VOTURNOVER", //成交量
            "VATURNOVER", //成交金额
            "TCAP", //总市值
            "MCAP"//流通市值
    );

    public String getDealCsvUrl(Map<String, String> params) {
        String stockCode = params.containsKey("stockCode") ? params.get("stockCode") : "";
        String startDate = params.containsKey("startDate") ? params.get("startDate") : "";
        String endDate = params.containsKey("endDate") ? params.get("endDate") : "";
        return this.demoUrl + "?"
                + "code=" + stockCode
                + "&start=" + startDate
                + "&end=" + endDate
                + "&fields=" + this.getParamField();
    }

    private String getParamField() {
        int filedSize = this.fieldList.size();
        String filedStr = "";
        for (int i = 0; i < filedSize; i++) {
            filedStr += this.fieldList.get(i) + ",";
        }
        return filedStr.length() > 1 ? filedStr.substring(0, filedStr.length() - 1) : "";
    }
}
