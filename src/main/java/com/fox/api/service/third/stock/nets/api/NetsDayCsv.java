package com.fox.api.service.third.stock.nets.api;

import com.fox.api.entity.dto.http.HttpResponseDto;
import com.fox.api.entity.po.third.stock.StockDealDayPo;
import com.fox.api.util.DateUtil;
import com.fox.api.util.FileUtil;
import com.fox.api.util.HttpUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 股票按天成交信息
 * @author lusongsong
 * @date 2020/3/5 18:13
 */
public class NetsDayCsv extends NetsStockBaseApi {
    /**
     * 样例链接
     * http://quotes.money.163.com/service/chddata.html?code=0603383&start=20200101&end=20200501&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP
     */
    private static String demoUrl = "http://quotes.money.163.com/service/chddata.html";
    /**
     * 字段列表
     * TCLOSE:收盘价
     * HIGH:最高价
     * LOW:最低价
     * TOPEN:开盘价
     * LCLOSE:前收盘价
     * CHG:涨跌额
     * PCHG:涨跌幅
     * TURNOVER:换手率
     * VOTURNOVER:成交量
     * VATURNOVER:成交金额
     * TCAP:总市值
     * MCAP:流通市值
     */
    private static String fieldList = "TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP";

    /**
     * 稳定日期
     */
    protected Integer stableDay = 60;

    /**
     * 获取本地文件保存路径
     * @param params
     * @return
     */
    protected String saveFile(Map<String, String> params) {
        String startDate = params.containsKey("startDate") ? params.get("startDate") : "";
        String endDate = params.containsKey("endDate") ? params.get("endDate") : "";
        if (!startDate.endsWith("-01-01") || !endDate.endsWith("-12-31")) {
            return "";
        }
        String startYear = startDate.replace("-01-01", "");
        String endYear = endDate.replace("-12-31", "");
        if (!startYear.equals(endYear)) {
            return "";
        }
        String stableDate = DateUtil.getRelateDate(0, 0, -stableDay, DateUtil.DATE_FORMAT_1);
        try {
            if (DateUtil.compare(endDate, stableDate, DateUtil.DATE_FORMAT_1)) {
                String filePath = saveFilePath(params, getClass().getSimpleName());
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(filePath);
                stringBuffer.append("/");
                stringBuffer.append(startYear);
                stringBuffer.append(".txt");
                return stringBuffer.toString().replace("//", "/");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 构建csv文件地址
     * @param params
     * @return
     */
    private String getDealCsvUrl(Map<String, String> params) {
        String stockCode = params.containsKey("netsStockCode") ? params.get("netsStockCode") : "";
        String startDate = params.containsKey("startDate") ? params.get("startDate") : "";
        startDate = DateUtil.dateStrFormatChange(
                startDate, DateUtil.DATE_FORMAT_1, DateUtil.DATE_FORMAT_2
        );
        String endDate = params.containsKey("endDate") ? params.get("endDate") : "";
        endDate = DateUtil.dateStrFormatChange(
                endDate, DateUtil.DATE_FORMAT_1, DateUtil.DATE_FORMAT_2
        );
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(demoUrl);
        stringBuffer.append("?fields=");
        stringBuffer.append(fieldList);
        stringBuffer.append("&code=");
        stringBuffer.append(stockCode);
        stringBuffer.append("&start=");
        stringBuffer.append(startDate);
        stringBuffer.append("&end=");
        stringBuffer.append(endDate);
        return stringBuffer.toString();
    }

    /**
     * 获取交易信息
     * @param params
     * @return
     */
    public List<StockDealDayPo> getDealDayInfo(Map<String, String> params) {
        List<StockDealDayPo> stockDealDayPoList = new ArrayList<>();
        String filePath = saveFile(params);
        filePath = null == filePath ? "" : filePath;
        try {
            String dealString = null;
            Boolean writeFile = false;
            if (!filePath.isEmpty()) {
                dealString = FileUtil.read(filePath);
            }
            if (null == dealString || dealString.isEmpty()) {
                writeFile = true;
                String csvFileUrl = getDealCsvUrl(params);
                HttpUtil httpUtil = new HttpUtil();
                httpUtil.setUrl(csvFileUrl).setOriCharset("GBK");
                HttpResponseDto httpResponse = null;
                httpResponse = httpUtil.request();
                dealString = httpResponse.getContent();
            }
            if (null != dealString && dealString.length() > 0) {
                String[] dealStringArr = dealString.split("\n");
                for (int i = 1; i < dealStringArr.length; i++) {
                    String[] dayDealStringArr = dealStringArr[i].split(",");
                    StockDealDayPo stockDealDayPo = new StockDealDayPo();
                    for (int j = 0; j < dayDealStringArr.length; j++) {
                        String subStr = dayDealStringArr[j];
                        if (null == subStr || subStr.length() == 0) {
                            continue;
                        }
                        subStr = "None".equals(subStr) ? "0" : subStr;
                        switch (j) {
                            case 0:
                                stockDealDayPo.setDt(subStr);
                                break;
                            case 1:
                                stockDealDayPo.setStockCode(subStr.replace("'", ""));
                                break;
                            case 2:
                                stockDealDayPo.setStockName(subStr.replace(" ", ""));
                                break;
                            case 3:
                                stockDealDayPo.setClosePrice(BigDecimal.valueOf(Double.valueOf(subStr)));
                                break;
                            case 4:
                                stockDealDayPo.setHighestPrice(BigDecimal.valueOf(Double.valueOf(subStr)));
                                break;
                            case 5:
                                stockDealDayPo.setLowestPrice(BigDecimal.valueOf(Double.valueOf(subStr)));
                                break;
                            case 6:
                                stockDealDayPo.setOpenPrice(BigDecimal.valueOf(Double.valueOf(subStr)));
                                break;
                            case 7:
                                stockDealDayPo.setPreClosePrice(BigDecimal.valueOf(Double.valueOf(subStr)));
                                break;
                            case 8:
                                stockDealDayPo.setUptickPrice(BigDecimal.valueOf(Double.valueOf(subStr)));
                                break;
                            case 9:
                                stockDealDayPo.setUptickRate(BigDecimal.valueOf(Double.valueOf(subStr)));
                                break;
                            case 10:
                                stockDealDayPo.setTurnoverRate(BigDecimal.valueOf(Double.valueOf(subStr)));
                                break;
                            case 11:
                                stockDealDayPo.setDealNum(Long.valueOf(subStr));
                                break;
                            case 12:
                                stockDealDayPo.setDealMoney(BigDecimal.valueOf(Double.valueOf(subStr)));
                                break;
                            case 13:
                                stockDealDayPo.setTotalValue(BigDecimal.valueOf(Double.valueOf(subStr)));
                                break;
                            case 14:
                                stockDealDayPo.setCircValue(BigDecimal.valueOf(Double.valueOf(subStr)));
                                break;
                        }
                    }
                    stockDealDayPoList.add(stockDealDayPo);
                }
            }
            Collections.reverse(stockDealDayPoList);

            if (writeFile && null != dealString && !dealString.isEmpty()
                    && !stockDealDayPoList.isEmpty() && !filePath.isEmpty()) {
                FileUtil.coverWrite(filePath, dealString);
            }
            return stockDealDayPoList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stockDealDayPoList;
    }
}
