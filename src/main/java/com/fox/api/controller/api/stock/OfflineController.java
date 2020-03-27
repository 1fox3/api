package com.fox.api.controller.api.stock;

import com.fox.api.entity.dto.result.ResultDto;
import com.fox.api.enums.code.ReturnCode;
import com.fox.api.service.stock.StockOfflineService;
import com.fox.api.entity.po.third.stock.StockDayLinePo;
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
    public ResultDto realtime(int stockId) {
        StockDayLinePo stockDayLineEntity = stockOfflineService.line(stockId);
        if (null == stockDayLineEntity) {
            return ResultDto.fail(ReturnCode.FAIL);
        }
        return ResultDto.success(stockDayLineEntity);
    }
}
