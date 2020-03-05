package com.fox.api.service.stock;

import com.fox.api.service.stock.entity.PageInfo;
import com.fox.api.service.stock.entity.updown.StockUpDown;

import java.util.List;

public interface StockUpDownService {
    List<StockUpDown> getList(String orderBy, PageInfo pageInfo);
}
