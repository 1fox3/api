package com.fox.api.controller.api.stock;

import com.fox.api.constant.stock.StockConst;
import com.fox.api.entity.dto.result.ResultDto;
import com.fox.api.util.StockUtil;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 股市
 * @author lusongsong
 * @date 2020-08-20 14:52
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/stock/stockMarket/")
public class StockMarketController {

    /**
     * 获取最近的交易日
     * @return
     */
    @RequestMapping("lastDealDate")
    public ResultDto lastDealDate() {
        return ResultDto.success(StockUtil.lastDealDate(StockConst.SM_A));
    }
}
