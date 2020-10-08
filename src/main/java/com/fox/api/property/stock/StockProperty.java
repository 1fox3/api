package com.fox.api.property.stock;

import com.fox.api.entity.property.stock.StockCodeProperty;
import com.fox.api.entity.property.stock.StockKindInfoProperty;
import com.fox.api.entity.property.stock.StockMarketInfoProperty;
import com.fox.api.entity.property.stock.StockTypeInfoProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 股票相关配置
 * @author lusongsong
 * @date 2020/3/10 19:56
 */
@Component
@ConfigurationProperties("stock")
@Data
public class StockProperty {
    /**
     * 股票集市
     */
    Map<String, StockMarketInfoProperty> market;
    /**
     * 股票类型
     */
    Map<String, StockTypeInfoProperty> type;
    /**
     * 股票划分
     */
    Map<String, StockKindInfoProperty> kind;
    /**
     * 重点指数
     */
    List<StockCodeProperty> topIndex;
    /**
     * 数据参照指数
     */
    List<StockCodeProperty> demoIndex;
}
