package com.fox.api.service.third.stock.nets.api;

/**
 * 获取某日成交信息excel
 * @author lusongsong
 * @date 2020/3/5 18:13
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
        return demoUrl.replace("{stockCode}", stockCode)
                .replace("{year}", date.substring(0, 4))
                .replace("{date}", date);
    }
}
