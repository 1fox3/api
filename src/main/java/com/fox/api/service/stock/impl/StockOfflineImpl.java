package com.fox.api.service.stock.impl;

import com.fox.api.util.DateUtil;
import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.service.stock.StockOfflineService;
import com.fox.api.entity.po.third.stock.StockDayLinePo;
import com.fox.api.entity.po.third.stock.StockDealPo;
import com.fox.api.service.third.stock.nets.api.NetsDayLine;
import com.fox.api.service.third.stock.sina.api.SinaDeal;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockOfflineImpl extends StockBaseImpl implements StockOfflineService {
    @Override
    public StockDayLinePo line(Integer stockId) {
        return this.line(stockId, DateUtil.getRelateDate(-1, 0, 0, DateUtil.DATE_FORMAT_1));
    }

    @Override
    public StockDayLinePo line(Integer stockId, String startDate) {
        return this.line(stockId, startDate, DateUtil.getRelateDate(0, 0, 0, DateUtil.DATE_FORMAT_1));
    }

    @Override
    public StockDayLinePo line(Integer stockId, String startDate, String endDate) {
        NetsDayLine netsDayLine = new NetsDayLine();
        return netsDayLine.getDayLine(this.getNetsStockInfoMap(stockId), startDate, endDate);
    }

    @Override
    public StockDayLinePo line(Integer stockId, Integer dayLen) {
        StockEntity stockEntity = this.getStockEntity(stockId);
        StockDayLinePo stockDayLineEntity = new StockDayLinePo();
        stockDayLineEntity.setStockCode(stockEntity.getStockCode());
        stockDayLineEntity.setStockName(stockEntity.getStockName());

        SinaDeal sinaDeal = new SinaDeal();
        List<StockDealPo> list = sinaDeal.getDealList(this.getSinaStockCode(stockId), 240, dayLen);
        stockDayLineEntity.setLineNode(list);
        return stockDayLineEntity;
    }
}
