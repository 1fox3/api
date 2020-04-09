package com.fox.api.service.stock.impl;

import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.entity.dto.http.HttpResponseDto;
import com.fox.api.service.stock.StockInfoService;
import com.fox.api.util.HttpUtil;
import org.springframework.stereotype.Service;

@Service
public class StockInfoImpl extends StockBaseImpl implements StockInfoService {

    @Override
    public String getInfoFromStockExchange(Integer stockId) {
        StockEntity stockEntity = this.getStockEntity(stockId);
        Integer stockMarket = stockEntity.getStockMarket();
        if (1 == stockMarket) {
            HttpUtil httpUtil = new HttpUtil();
            httpUtil.setUrl("http://query.sse.com.cn/commonQuery.do");
            httpUtil.setParam("sqlId", "COMMON_SSE_ZQPZ_GP_GPLB_C");
            httpUtil.setParam("productid", stockEntity.getStockCode());
            httpUtil.setHeader("Referer",
                    "http://www.sse.com.cn/assortment/stock/list/info/company/index.shtml?COMPANY_CODE=" + stockEntity.getStockCode()
            );
            try {
                HttpResponseDto httpResponse = httpUtil.request();
                return httpResponse.getContent();
            } catch (Exception e) {}
        }
        if (2 == stockMarket) {
            HttpUtil httpUtil = new HttpUtil();
            httpUtil.setUrl("http://www.szse.cn/api/report/index/companyGeneralization");
            httpUtil.setParam("secCode", stockEntity.getStockCode());
            try {
                HttpResponseDto httpResponse = httpUtil.request();
                return httpResponse.getContent();
            } catch (Exception e) {}
        }
        return "";
    }
}
