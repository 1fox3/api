package com.fox.api.controller.api.stock;

import com.fox.api.controller.entity.result.Result;
import com.fox.api.controller.enums.code.ReturnCode;
import com.fox.api.service.stock.StockRealtimeService;
import com.fox.api.service.third.stock.entity.StockRealtimeEntity;
import com.fox.api.service.third.stock.entity.StockRealtimeLineEntity;
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
    public Result realtime(int stockId) {
        StockRealtimeEntity stockRealtimeEntity = stockRealtimeService.info(stockId);
        if (null == stockRealtimeEntity) {
            return Result.fail(ReturnCode.FAIL);
        }
        return Result.success(stockRealtimeEntity);
    }

    @RequestMapping("/stock/realtime/priceList")
    public Result priceList(int stockId) {
        StockRealtimeEntity stockRealtimeEntity = stockRealtimeService.info(stockId);
        if (null == stockRealtimeEntity) {
            return Result.fail(ReturnCode.FAIL);
        }
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("sell", stockRealtimeEntity.getSellPriceList());
        map.put("buy", stockRealtimeEntity.getBuyPriceList());
        return Result.success(map);
    }

    @RequestMapping("/stock/realtime/line")
    public Result lint(int stockId) {
        StockRealtimeLineEntity stockRealtimeLineEntity = stockRealtimeService.line(stockId);
        if (null == stockRealtimeLineEntity) {
            return Result.fail(ReturnCode.FAIL);
        }
        return Result.success(stockRealtimeLineEntity);
    }
}
