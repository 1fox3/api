package com.fox.api.service.stock;

import com.fox.api.service.stock.entity.PageInfo;
import com.fox.api.service.stock.entity.updown.StockLimitUpDown;

import java.util.List;

public interface StockLimitUpDownService {
    List<StockLimitUpDown> getList(Integer type, PageInfo pageInfo);

    Integer countByType(Integer type);
}
