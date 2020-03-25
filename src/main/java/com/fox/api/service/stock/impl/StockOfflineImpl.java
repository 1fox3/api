package com.fox.api.service.stock.impl;

import com.fox.api.util.DateUtil;
import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.service.stock.StockOfflineService;
import com.fox.api.service.third.stock.entity.StockDayLineEntity;
import com.fox.api.service.third.stock.entity.StockDealEntity;
import com.fox.api.service.third.stock.nets.api.NetsDayLine;
import com.fox.api.service.third.stock.sina.api.SinaDeal;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockOfflineImpl extends StockBaseImpl implements StockOfflineService {
    @Override
    public StockDayLineEntity line(int stockId) {
        return this.line(stockId, DateUtil.getRelateDate(-1, 0, 0, DateUtil.DATE_FORMAT_1));
    }

    @Override
    public StockDayLineEntity line(int stockId, String startDate) {
        return this.line(stockId, startDate, DateUtil.getRelateDate(0, 0, 0, DateUtil.DATE_FORMAT_1));
    }

    @Override
    public StockDayLineEntity line(int stockId, String startDate, String endDate) {
        NetsDayLine netsDayLine = new NetsDayLine();
        return netsDayLine.getDayLine(this.getNetsStockInfoMap(stockId), startDate, endDate);
    }

    @Override
    public StockDayLineEntity line(int stockId, int dayLen) {
        StockEntity stockEntity = this.getStockEntity(stockId);
        StockDayLineEntity stockDayLineEntity = new StockDayLineEntity();
        stockDayLineEntity.setStockCode(stockEntity.getStockCode());
        stockDayLineEntity.setStockName(stockEntity.getStockName());

        SinaDeal sinaDeal = new SinaDeal();
        System.out.println(this.getSinaStockCode(stockId));
        System.out.println(dayLen);
        List<StockDealEntity> list = sinaDeal.getDealList(this.getSinaStockCode(stockId), 240, dayLen);
        stockDayLineEntity.setLineNode(list);
        return stockDayLineEntity;
    }
}
