package com.fox.api.service.third.stock.nets.api;

/**
 * 获取某日成交信息excel
 */
public class NetsDealExcel extends NetsStockBaseApi {
    //样例链接
    private static String demoUrl = "http://quotes.money.163.com/cjmx/{year}/{date}/{stockCode}.xls";

    /**
     * 获取下载成交信息excel链接
     * @param stockCode
     * @param date
     * @return
     */
    public String getDealExcelUrl(String stockCode, String date) {
        return this.demoUrl.replace("{stockCode}", stockCode)
                .replace("{year}", date.substring(0, 4))
                .replace("{date}", date);
    }
}
