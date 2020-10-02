package com.fox.api.entity.property.stock;

import lombok.Data;

import java.util.List;

/**
 * 股票划分配置
 * @author lusongsong
 * @date 2020/3/10 19:43
 */
@Data
public class StockKindInfoProperty {
    String stockKindName;
    Integer stockMarket;
    Integer stockType;
    Integer stockKind;
    List<String> perCode;
}
