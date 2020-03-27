package com.fox.api.controller.api.stock;

import com.fox.api.entity.dto.result.ResultDto;
import com.fox.api.enums.code.ReturnCode;
import com.fox.api.service.stock.StockRealtimeService;
import com.fox.api.entity.po.third.stock.StockRealtimePo;
import com.fox.api.entity.po.third.stock.StockRealtimeLinePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@CrossOrigin(origins = "*")//处理跨域问题
@RestController
public class RealtimeController {

    @Autowired
    private StockRealtimeService stockRealtimeService;

    @RequestMapping("/stock/realtime/info")
    public ResultDto realtime(int stockId) {
        StockRealtimePo stockRealtimeEntity = stockRealtimeService.info(stockId);
        if (null == stockRealtimeEntity) {
            return ResultDto.fail(ReturnCode.FAIL);
        }
        return ResultDto.success(stockRealtimeEntity);
    }

    @RequestMapping("/stock/realtime/priceList")
    public ResultDto priceList(int stockId) {
        StockRealtimePo stockRealtimeEntity = stockRealtimeService.info(stockId);
        if (null == stockRealtimeEntity) {
            return ResultDto.fail(ReturnCode.FAIL);
        }
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("sell", stockRealtimeEntity.getSellPriceList());
        map.put("buy", stockRealtimeEntity.getBuyPriceList());
        return ResultDto.success(map);
    }

    @RequestMapping("/stock/realtime/line")
    public ResultDto lint(int stockId) {
        StockRealtimeLinePo stockRealtimeLineEntity = stockRealtimeService.line(stockId);
        if (null == stockRealtimeLineEntity) {
            return ResultDto.fail(ReturnCode.FAIL);
        }
        return ResultDto.success(stockRealtimeLineEntity);
    }
}
