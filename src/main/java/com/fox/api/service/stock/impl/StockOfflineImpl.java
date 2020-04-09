package com.fox.api.service.stock.impl;

import com.fox.api.dao.stock.entity.StockDealDayEntity;
import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.entity.dto.stock.offline.StockDealDayDto;
import com.fox.api.entity.dto.stock.offline.StockDealDayLineDto;
import com.fox.api.entity.po.third.stock.StockDayLinePo;
import com.fox.api.entity.po.third.stock.StockDealNumPo;
import com.fox.api.entity.po.third.stock.StockDealPo;
import com.fox.api.service.stock.StockOfflineService;
import com.fox.api.service.third.stock.nets.api.NetsDayLine;
import com.fox.api.service.third.stock.sina.api.SinaDealRatio;
import com.fox.api.util.DateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class StockOfflineImpl extends StockBaseImpl implements StockOfflineService {

    @Override
    public StockDealDayLineDto line(Integer stockId, String startDate) {
        return this.line(stockId, startDate, DateUtil.getCurrentDate());
    }

    @Override
    public StockDealDayLineDto line(Integer stockId, String startDate, String endDate) {
        StockDealDayLineDto stockDealDayLineDto = new StockDealDayLineDto();
        StockEntity stockEntity = this.stockMapper.getById(stockId);
        if (null == stockEntity) {
            return stockDealDayLineDto;
        }
        stockDealDayLineDto.setStockName(stockEntity.getStockName());
        stockDealDayLineDto.setStockCode(stockEntity.getStockCode());

        List<StockDealDayEntity> dealDayList = this.stockDealDayMapper.getByDate(
                stockId, startDate, endDate, 0
        );
        List<StockDealDayDto> stockDealDayList = new LinkedList<>();
        if (null != dealDayList && 0 < dealDayList.size()) {
            for (StockDealDayEntity stockDealDayEntity : dealDayList) {
                StockDealDayDto stockDealDayDto = new StockDealDayDto();
                BeanUtils.copyProperties(stockDealDayEntity, stockDealDayDto);
                stockDealDayList.add(stockDealDayDto);
            }
            stockDealDayLineDto.setLineNode(stockDealDayList);
            return stockDealDayLineDto;
        } else {
            NetsDayLine netsDayLine = new NetsDayLine();
            StockDayLinePo stockDayLinePo = netsDayLine.getDayLine(this.getNetsStockInfoMap(stockId), startDate, endDate);
            List<StockDealPo> stockDealPos = stockDayLinePo.getLineNode();
            if (null != stockDealPos && 0 < stockDealPos.size()) {
                for (StockDealPo stockDealPo : stockDealPos) {
                    StockDealDayDto stockDealDayDto = new StockDealDayDto();
                    BeanUtils.copyProperties(stockDealPo, stockDealDayDto);
                    stockDealDayDto.setDt(stockDealPo.getDateTime());
                    stockDealDayList.add(stockDealDayDto);
                }
            }
        }

        return stockDealDayLineDto;
    }

    /**
     * 获取价格成交比例
     * @param stockId
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    public List<StockDealNumPo> dealRatio(Integer stockId, String startDate, String endDate) {
        SinaDealRatio sinaDealRatio = new SinaDealRatio();
        return sinaDealRatio.getDealRatio(this.getSinaStockCode(stockId), startDate, endDate);
    }
}
