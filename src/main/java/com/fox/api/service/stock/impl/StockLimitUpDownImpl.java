package com.fox.api.service.stock.impl;

import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.dao.stock.entity.StockLimitUpDownEntity;
import com.fox.api.dao.stock.mapper.StockLimitUpDownMapper;
import com.fox.api.service.stock.StockLimitUpDownService;
import com.fox.api.entity.po.PageInfoPo;
import com.fox.api.entity.dto.stock.updown.StockLimitUpDownDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class StockLimitUpDownImpl extends StockBaseImpl implements StockLimitUpDownService {
    @Autowired
    private StockLimitUpDownMapper stockLimitUpDownMapper;

    @Override
    public List<StockLimitUpDownDto> getList(Integer type, PageInfoPo pageInfo) {
        List<StockLimitUpDownEntity> dataList = stockLimitUpDownMapper.getList(type, pageInfo.getLimitStr());
        List<StockLimitUpDownDto> list = new LinkedList<>();
        if (null != dataList && dataList.size() > 0) {
            for (StockLimitUpDownEntity stockLimitUpDownEntity : dataList) {
                StockLimitUpDownDto stockLimitUpDown = new StockLimitUpDownDto();
                BeanUtils.copyProperties(stockLimitUpDownEntity, stockLimitUpDown);
                StockEntity stockEntity = this.getStockEntity(stockLimitUpDownEntity.getStockId());
                if (null != stockEntity) {
                    BeanUtils.copyProperties(stockEntity, stockLimitUpDown);
                }
                list.add(stockLimitUpDown);
            }
        }
        return list;
    }

    @Override
    public Integer countByType(Integer type) {
        return stockLimitUpDownMapper.countByType(type);
    }


}
