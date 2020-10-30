package com.fox.api.service.third.stock.nets.api;

/**
 * 历史成交明细excel文件(接口不可用)
 *
 * @author lusongsong
 * @date 2020/3/5 18:13
 */
public class NetsDealExcel extends NetsStockBaseApi {
    /**
     * 样例链接 http://quotes.money.163.com/cjmx/2019/20191211/0603383.xls
     */
    private static String demoUrl = "http://quotes.money.163.com/cjmx/{year}/{date}/{stockCode}.xls";

    /**
     * 获取下载成交信息excel链接
     *
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
