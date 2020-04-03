package com.fox.api.property.stock;

import com.fox.api.entity.property.stock.StockKindInfoProperty;
import com.fox.api.entity.property.stock.StockMarketInfoProperty;
import com.fox.api.entity.property.stock.StockTypeInfoProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties("stock")
@Data
public class StockProperty {
    private Map<String, StockMarketInfoProperty> market;
    private Map<String, StockTypeInfoProperty> type;
    private Map<String, StockKindInfoProperty> kind;
    private List<Integer> topIndex;
}
