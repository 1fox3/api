package com.fox.api.service.stock.impl;

import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.dao.stock.entity.StockUpDownEntity;
import com.fox.api.dao.stock.mapper.StockUpDownMapper;
import com.fox.api.service.stock.StockUpDownService;
import com.fox.api.entity.po.PageInfoPo;
import com.fox.api.entity.dto.stock.updown.StockUpDownDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class StockUpDownImpl extends StockBaseImpl implements StockUpDownService {

    @Autowired
    private StockUpDownMapper stockUpDownMapper;

    @Override
    public List<StockUpDownDto> getList(String orderBy, PageInfoPo pageInfo) {
        List<StockUpDownEntity> dataList = stockUpDownMapper.getList(orderBy, pageInfo.getLimitStr());
        List<StockUpDownDto> list = new LinkedList<>();
        if (null != dataList && dataList.size() > 0) {
            for (StockUpDownEntity stockUpDownEntity : dataList) {
                StockUpDownDto stockUpDown = new StockUpDownDto();
                BeanUtils.copyProperties(stockUpDownEntity, stockUpDown);
                StockEntity stockEntity = this.getStockEntity(stockUpDownEntity.getStockId());
                if (null != stockEntity) {
                    BeanUtils.copyProperties(stockEntity, stockUpDown);
                }
                list.add(stockUpDown);
            }
        }
        return list;
    }
}
