package com.fox.api.service.stock.impl;

import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.dao.stock.entity.StockInfoEntity;
import com.fox.api.entity.dto.http.HttpResponseDto;
import com.fox.api.exception.self.ServiceException;
import com.fox.api.service.stock.StockInfoService;
import com.fox.api.util.HttpUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@CacheConfig(cacheNames = {"StockInfoService"})
/**
 * 股票信息
 * @author lusongsong
 */
public class StockInfoImpl extends StockBaseImpl implements StockInfoService {

    /**
     * 处理上交所请求返回数据
     * @param httpUtil
     * @return
     */
    private JSONObject handleSHStockInfoHttpResponse(HttpUtil httpUtil) {
        try {
            HttpResponseDto httpResponseDto = httpUtil.request();
            String responseContent = httpResponseDto.getContent();
            if (null != responseContent && !responseContent.equals("")) {
                JSONObject baseObject = JSONObject.fromObject(responseContent);
                if (!baseObject.isNullObject() && baseObject.containsKey("result")) {
                    JSONArray resultArray = baseObject.getJSONArray("result");
                    if (null != resultArray && 1 == resultArray.size()) {
                        return resultArray.getJSONObject(0);
                    }
                }
            }
        } catch (Exception e) {}
        return null;
    }

    /**
     * 从上交所获取股票信息
     * @param stockEntity
     * @return
     */
    private StockInfoEntity getSHStockInfo(StockEntity stockEntity) {
        StockInfoEntity stockInfoEntity = new StockInfoEntity();
        stockInfoEntity.setStockId(stockEntity.getId());
        stockInfoEntity.setStockMarket(stockEntity.getStockMarket());
        stockInfoEntity.setStockCode(stockEntity.getStockCode());
        stockInfoEntity.setStockName(stockEntity.getStockName());
        stockInfoEntity.setStockNameEn(stockEntity.getStockNameEn());
        String headerReferer =
                "http://www.sse.com.cn/assortment/stock/list/info/company/index.shtml?COMPANY_CODE="
                 + stockEntity.getStockCode();
        //基本信息
        HttpUtil baseInfoHttpUtil = new HttpUtil();
        baseInfoHttpUtil.setUrl("http://query.sse.com.cn/commonQuery.do");
        baseInfoHttpUtil.setParam("sqlId", "COMMON_SSE_ZQPZ_GP_GPLB_C");
        baseInfoHttpUtil.setParam("productid", stockEntity.getStockCode());
        baseInfoHttpUtil.setHeader("Referer", headerReferer);
        JSONObject baseInfoObject = this.handleSHStockInfoHttpResponse(baseInfoHttpUtil);
        if (null != baseInfoObject) {
            if (baseInfoObject.containsKey("COMPANY_ABBR")) {
                stockInfoEntity.setStockName(baseInfoObject.getString("COMPANY_ABBR"));
            }
            if (baseInfoObject.containsKey("ENGLISH_ABBR")) {
                stockInfoEntity.setStockNameEn(baseInfoObject.getString("ENGLISH_ABBR"));
            }
            if (baseInfoObject.containsKey("FULLNAME")) {
                stockInfoEntity.setStockFullName(baseInfoObject.getString("FULLNAME"));
            }
            if (baseInfoObject.containsKey("FULL_NAME_IN_ENGLISH")) {
                stockInfoEntity.setStockFullNameEn(baseInfoObject.getString("FULL_NAME_IN_ENGLISH"));
            }
            if (baseInfoObject.containsKey("LEGAL_REPRESENTATIVE")) {
                stockInfoEntity.setStockLegal(baseInfoObject.getString("LEGAL_REPRESENTATIVE").trim());
            }
            if (baseInfoObject.containsKey("COMPANY_ADDRESS")) {
                stockInfoEntity.setStockRegisterAddress(baseInfoObject.getString("COMPANY_ADDRESS"));
            }
            if (baseInfoObject.containsKey("OFFICE_ADDRESS")) {
                stockInfoEntity.setStockConnectAddress(baseInfoObject.getString("OFFICE_ADDRESS"));
            }
            if (baseInfoObject.containsKey("E_MAIL_ADDRESS")) {
                stockInfoEntity.setStockEmail(baseInfoObject.getString("E_MAIL_ADDRESS"));
            }
            if (baseInfoObject.containsKey("WWW_ADDRESS")) {
                stockInfoEntity.setStockWebsite(baseInfoObject.getString("WWW_ADDRESS"));
            }
            if (baseInfoObject.containsKey("SSE_CODE_DESC")) {
                stockInfoEntity.setStockIndustry(baseInfoObject.getString("SSE_CODE_DESC"));
            }
            if (baseInfoObject.containsKey("CSRC_CODE_DESC") && baseInfoObject.containsKey("CSRC_GREAT_CODE_DESC")) {
                stockInfoEntity.setStockCarc(baseInfoObject.getString("CSRC_CODE_DESC") + "/" + baseInfoObject.getString("CSRC_GREAT_CODE_DESC"));
            }
            if (baseInfoObject.containsKey("AREA_NAME_DESC")) {
                stockInfoEntity.setStockProvince(baseInfoObject.getString("AREA_NAME_DESC"));
            }
        }
        //股本信息
        HttpUtil equityHttpUtila = new HttpUtil();
        equityHttpUtila.setUrl("http://query.sse.com.cn/commonQuery.do");
        equityHttpUtila.setParam("sqlId", "COMMON_SSE_CP_GPLB_GPGK_GBJG_C");
        equityHttpUtila.setParam("companyCode", stockEntity.getStockCode());
        equityHttpUtila.setHeader("Referer", headerReferer);
        JSONObject equityInfoObject = this.handleSHStockInfoHttpResponse(equityHttpUtila);
        if (null != equityInfoObject) {
            if (equityInfoObject.containsKey("DOMESTIC_SHARES")) {
                stockInfoEntity.setStockTotalEquity(equityInfoObject.getDouble("DOMESTIC_SHARES"));
            }
            if (equityInfoObject.containsKey("UNLIMITED_SHARES")) {
                stockInfoEntity.setStockCircEquity(equityInfoObject.getDouble("UNLIMITED_SHARES"));
            }
        }
        //上市日期
        HttpUtil onDateHttpUtila = new HttpUtil();
        onDateHttpUtila.setUrl("http://query.sse.com.cn/commonQuery.do");
        onDateHttpUtila.setParam("sqlId", "COMMON_SSE_ZQPZ_GP_GPLB_AGSSR_C");
        onDateHttpUtila.setParam("productid", stockEntity.getStockCode());
        onDateHttpUtila.setHeader("Referer", headerReferer);
        JSONObject onDateObject = this.handleSHStockInfoHttpResponse(onDateHttpUtila);
        if (null != onDateObject) {
            if (onDateObject.containsKey("LISTINGDATEA")) {
                stockInfoEntity.setStockOnDate(onDateObject.getString("LISTINGDATEA"));
            }
        }
        return stockInfoEntity;
    }

    /**
     * 从深圳交易所获取股票信息
     * @param stockEntity
     * @return
     */
    private StockInfoEntity getSZStockInfo(StockEntity stockEntity) {
        StockInfoEntity stockInfoEntity = new StockInfoEntity();
        stockInfoEntity.setStockId(stockEntity.getId());
        stockInfoEntity.setStockMarket(stockEntity.getStockMarket());
        stockInfoEntity.setStockCode(stockEntity.getStockCode());
        stockInfoEntity.setStockName(stockEntity.getStockName());
        stockInfoEntity.setStockNameEn(stockEntity.getStockNameEn());
        try {
            HttpUtil httpUtil = new HttpUtil();
            httpUtil.setUrl("http://www.szse.cn/api/report/index/companyGeneralization");
            httpUtil.setParam("secCode", stockEntity.getStockCode());
            HttpResponseDto httpResponse = httpUtil.request();
            String responseContent = httpResponse.getContent();
            if (null != responseContent && !responseContent.equals("")) {
                JSONObject baseObject = JSONObject.fromObject(responseContent);
                if (!baseObject.isNullObject() && baseObject.containsKey("data")) {
                    JSONObject dataObject = baseObject.getJSONObject("data");
                    if (null != dataObject) {
                        String typeStr = 9 == stockEntity.getStockKind() ? "b" : "a";

                        if (dataObject.containsKey(typeStr + "gjc")) {
                            stockInfoEntity.setStockName(dataObject.getString(typeStr + "gjc"));
                        }
                        if (dataObject.containsKey(typeStr + "gdm")) {
                            stockInfoEntity.setStockCode(dataObject.getString(typeStr + "gdm"));
                        }
                        if (dataObject.containsKey(typeStr + "gzgb")) {
                            stockInfoEntity.setStockTotalEquity(Double.valueOf(dataObject.getString(typeStr + "gzgb").replace(",", "")));
                        }
                        if (dataObject.containsKey(typeStr + "gltgb")) {
                            stockInfoEntity.setStockCircEquity(Double.valueOf(dataObject.getString(typeStr + "gltgb").replace(",", "")));
                        }
                        if (dataObject.containsKey(typeStr + "gssrq")) {
                            stockInfoEntity.setStockOnDate(dataObject.getString(typeStr + "gssrq"));
                        }
                        if (dataObject.containsKey("gsqc")) {
                            stockInfoEntity.setStockFullName(dataObject.getString("gsqc"));
                        }
                        if (dataObject.containsKey("ywqc")) {
                            stockInfoEntity.setStockFullNameEn(dataObject.getString("ywqc"));
                        }
                        if (dataObject.containsKey("zcdz")) {
                            stockInfoEntity.setStockRegisterAddress(dataObject.getString("zcdz"));
                        }
                        if (dataObject.containsKey("http")) {
                            stockInfoEntity.setStockWebsite(dataObject.getString("http"));
                        }
                        if (dataObject.containsKey("dldq")) {
                            stockInfoEntity.setStockArea(dataObject.getString("dldq"));
                        }
                        if (dataObject.containsKey("sheng")) {
                            stockInfoEntity.setStockProvince(dataObject.getString("sheng"));
                        }
                        if (dataObject.containsKey("shi")) {
                            stockInfoEntity.setStockCity(dataObject.getString("shi"));
                        }
                        if (dataObject.containsKey("sshymc")) {
                            stockInfoEntity.setStockIndustry(dataObject.getString("sshymc"));
                        }
                    }
                }
            }
        } catch (Exception e) {}
        return stockInfoEntity;
    }

    /**
     * 重交易所网站获取股票信息
     * @param stockId
     * @return
     */
    @Override
    public StockInfoEntity getInfoFromStockExchange(Integer stockId) {
        StockInfoEntity stockInfoEntity = new StockInfoEntity();
        StockEntity stockEntity = this.getStockEntity(stockId);
        Integer stockMarket = stockEntity.getStockMarket();
        if (1 == stockMarket) {
            return this.getSHStockInfo(stockEntity);
        }

        if (2 == stockMarket) {
            return this.getSZStockInfo(stockEntity);
        }
        return stockInfoEntity;
    }

    /**
     * 从数据苦衷获取信息
     * @param stockId
     * @return
     */
    @Override
    @Cacheable(key = "#stockId", cacheManager = "stockCacheManager")
    public StockInfoEntity getInfo(Integer stockId) {
        return this.stockInfoMapper.getByStockId(stockId);
    }

    /**
     * 搜索
     *
     * @param search
     * @return
     */
    @Override
    public List<Map<String, Object>> search(String search) {
        try {
            int nameLengthLimit = 2;
            int codeLengthLimit = 4;
            List<Map<String, Object>> searchList = new LinkedList<>();
            //空或者长度小于2，则不进行搜索
            if (null == search || search.length() < nameLengthLimit) {
                return searchList;
            }
            String pattern = "^\\d+$";
            String key = "stock_name";
            if (Pattern.matches(pattern, search)) {
                //如果是按照股票代码搜索，至少4位
                if (search.length() < codeLengthLimit) {
                    return searchList;
                }
                key = "stock_code";
            }
            List<StockInfoEntity> infoList = this.stockInfoMapper.search(search, key);

            for (StockInfoEntity stockInfoEntity : infoList) {
                Map<String, Object> infoMap = new LinkedHashMap<>(3);
                infoMap.put("stockId", stockInfoEntity.getStockId());
                infoMap.put("stockCode", stockInfoEntity.getStockCode());
                infoMap.put("stockName", stockInfoEntity.getStockName());
                searchList.add(infoMap);
            }
            return searchList;
        } catch (Exception e) {
            throw new ServiceException(1, e.getMessage());
        }
    }
}
