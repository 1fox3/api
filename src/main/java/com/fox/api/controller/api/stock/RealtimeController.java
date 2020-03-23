package com.fox.api.controller.api.stock;

import com.fox.api.controller.dto.result.ResultDTO;
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
    public ResultDTO realtime(int stockId) {
        StockRealtimeEntity stockRealtimeEntity = stockRealtimeService.info(stockId);
        if (null == stockRealtimeEntity) {
            return ResultDTO.fail(ReturnCode.FAIL);
        }
        return ResultDTO.success(stockRealtimeEntity);
    }

    @RequestMapping("/stock/realtime/priceList")
    public ResultDTO priceList(int stockId) {
        StockRealtimeEntity stockRealtimeEntity = stockRealtimeService.info(stockId);
        if (null == stockRealtimeEntity) {
            return ResultDTO.fail(ReturnCode.FAIL);
        }
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("sell", stockRealtimeEntity.getSellPriceList());
        map.put("buy", stockRealtimeEntity.getBuyPriceList());
        return ResultDTO.success(map);
    }

    @RequestMapping("/stock/realtime/line")
    public ResultDTO lint(int stockId) {
        StockRealtimeLineEntity stockRealtimeLineEntity = stockRealtimeService.line(stockId);
        if (null == stockRealtimeLineEntity) {
            return ResultDTO.fail(ReturnCode.FAIL);
        }
        return ResultDTO.success(stockRealtimeLineEntity);
    }
}
