package com.fox.api.service.stock;

import com.fox.api.entity.po.PageInfoPo;
import com.fox.api.entity.dto.stock.updown.StockLimitUpDownDto;

import java.util.List;

public interface StockLimitUpDownService {
    List<StockLimitUpDownDto> getList(Integer type, PageInfoPo pageInfo);

    Integer countByType(Integer type);
}
