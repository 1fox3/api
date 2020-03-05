package com.fox.api.controller.api.stock;

import com.fox.api.controller.entity.result.Result;
import com.fox.api.controller.enums.code.ReturnCode;
import com.fox.api.service.stock.StockOfflineService;
import com.fox.api.service.third.stock.entity.StockDayLineEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")//处理跨域问题
@RestController
public class OfflineController {
    @Autowired
    private StockOfflineService stockOfflineService;

    @RequestMapping("/stock/offline/line")
    public Result realtime(int stockId) {
        StockDayLineEntity stockDayLineEntity = stockOfflineService.line(stockId);
        if (null == stockDayLineEntity) {
            return Result.fail(ReturnCode.FAIL);
        }
        return Result.success(stockDayLineEntity);
    }
}
