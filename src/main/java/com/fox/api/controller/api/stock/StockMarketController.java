package com.fox.api.controller.api.stock;

import com.fox.api.entity.dto.result.ResultDto;
import com.fox.api.service.stock.StockDealDateService;
import com.fox.api.util.StockUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 股市
 *
 * @author lusongsong
 * @date 2020-08-20 14:52
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/stock/stockMarket/")
public class StockMarketController {
    @Autowired
    StockDealDateService stockDealDateService;

    /**
     * 获取最近的交易日
     *
     * @return
     */
    @RequestMapping("lastDealDate")
    public ResultDto lastDealDate(Integer stockMarket) {
        return ResultDto.success(StockUtil.lastDealDate(stockMarket));
    }

    /**
     * 股市近3个交易日(上一个，当前，下一个)
     *
     * @param stockMarket
     * @return
     */
    @RequestMapping("aroundDealDate")
    public ResultDto aroundDealDate(Integer stockMarket) {
        return ResultDto.success(stockDealDateService.around(stockMarket));
    }
}
