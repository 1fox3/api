package com.fox.api.service.stock.impl;

import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.dao.stock.entity.StockInfoEntity;
import com.fox.api.service.stock.StockInfoService;
import com.fox.api.util.StockUtil;
import com.fox.spider.stock.api.hk.HKStockInfoApi;
import com.fox.spider.stock.api.sh.SHStockInfoApi;
import com.fox.spider.stock.api.sz.SZStockInfoApi;
import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.po.hk.HKStockInfoPo;
import com.fox.spider.stock.entity.po.sh.SHStockInfoPo;
import com.fox.spider.stock.entity.po.sz.SZStockInfoPo;
import com.fox.spider.stock.entity.vo.StockVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 股票信息
 *
 * @author lusongsong
 * @date 2020/4/9 18:02
 */
@Service
@CacheConfig(cacheNames = {"StockInfoService"})
public class StockInfoImpl extends StockBaseImpl implements StockInfoService {
    /**
     * 沪市股票信息接口
     */
    @Autowired
    SHStockInfoApi shStockInfoApi;
    /**
     * 深市股票信息接口
     */
    @Autowired
    SZStockInfoApi szStockInfoApi;
    /**
     * 港股股票信息接口
     */
    @Autowired
    HKStockInfoApi hkStockInfoApi;

    /**
     * 从交易所网站获取股票信息
     *
     * @param stockVo
     * @return
     */
    @Override
    public StockInfoEntity getInfoFromStockExchange(StockVo stockVo) {
        if (null == stockVo) {
            return null;
        }
        StockInfoEntity stockInfoEntity = new StockInfoEntity();
        switch (stockVo.getStockMarket()) {
            case StockConst.SM_SH:
                SHStockInfoPo shStockInfoPo = shStockInfoApi.stockInfo(stockVo.getStockCode());
                if (null == shStockInfoPo) {
                    return null;
                }
                BeanUtils.copyProperties(shStockInfoPo, stockInfoEntity);
                break;
            case StockConst.SM_SZ:
                SZStockInfoPo szStockInfoPo = szStockInfoApi.stockInfo(stockVo.getStockCode());
                if (null == szStockInfoPo) {
                    return null;
                }
                BeanUtils.copyProperties(szStockInfoPo, stockInfoEntity);
                break;
            case StockConst.SM_HK:
                HKStockInfoPo hkStockInfoPo = hkStockInfoApi.stockInfo(
                        StockUtil.hkStockMarketToken(), stockVo.getStockCode()
                );
                if (null == hkStockInfoPo) {
                    return null;
                }
                BeanUtils.copyProperties(hkStockInfoPo, stockInfoEntity);
                break;
            default:
                return null;
        }
        if (null == stockInfoEntity.getStockWebsite()) {
            stockInfoEntity.setStockWebsite("");
        }
        if (null == stockInfoEntity.getStockLegal()) {
            stockInfoEntity.setStockLegal("");
        }
        StockEntity stockEntity = stockMapper.getByStockCode(stockVo.getStockCode(), stockVo.getStockMarket());
        if (null == stockInfoEntity.getStockNameEn() || stockInfoEntity.getStockNameEn().isEmpty()) {
            stockInfoEntity.setStockNameEn(stockEntity.getStockNameEn());
        }
        stockInfoEntity.setStockId(stockEntity.getId());
        return stockInfoEntity;
    }

    /**
     * 从数据苦衷获取信息
     *
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
    }
}
