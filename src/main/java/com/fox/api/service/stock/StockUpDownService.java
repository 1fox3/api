package com.fox.api.service.stock;

import com.fox.api.entity.po.PageInfoPo;
import com.fox.api.entity.dto.stock.updown.StockUpDownDto;

import java.util.List;

public interface StockUpDownService {
    List<StockUpDownDto> getList(String orderBy, PageInfoPo pageInfo);
}
