package com.fox.api.config.stock;

import com.fox.api.config.stock.entity.StockKindInfoEntity;
import com.fox.api.config.stock.entity.StockMarketInfoEntity;
import com.fox.api.config.stock.entity.StockTypeInfoEntity;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties("stock")
@Data
public class StockConfig {
    private Map<String, StockMarketInfoEntity> market;
    private Map<String, StockTypeInfoEntity> type;
    private Map<String, StockKindInfoEntity> kind;
}
