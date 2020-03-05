package com.fox.api.controller.api.stock;

import com.fox.api.controller.entity.result.Result;
import com.fox.api.controller.enums.code.ReturnCode;
import com.fox.api.service.stock.StockLimitUpDownService;
import com.fox.api.service.stock.StockUpDownService;
import com.fox.api.service.stock.entity.PageInfo;
import com.fox.api.service.stock.entity.updown.StockLimitUpDown;
import com.fox.api.service.stock.entity.updown.StockUpDown;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*")//处理跨域问题
@RestController
public class UpDownController {
    @Autowired
    StockUpDownService stockUpDownService;

    @Autowired
    StockLimitUpDownService stockLimitUpDownService;

    @RequestMapping("/stock/upDown/list")
    public Result list(String orderBy, PageInfo pageInfo) {
        orderBy = null == orderBy ? "d10_up DESC" : orderBy;
        List<StockUpDown> list = stockUpDownService.getList(orderBy, pageInfo);
        if (null == list) {
            return Result.fail(ReturnCode.FAIL);
        }
        return Result.success(list);
    }

    @RequestMapping("/stock/limitUpDown/list")
    public Result list(Integer type, PageInfo pageInfo) {
        type = null == type ? 1 : type;
        List<StockLimitUpDown> list = stockLimitUpDownService.getList(type, pageInfo);
        if (null == list) {
            return Result.fail(ReturnCode.FAIL);
        }
        return Result.success(list);
    }

    @RequestMapping("/stock/limitUpDown/count")
    public Result list(Integer type) {
        type = null == type ? 1 : type;
        Integer totalCount = stockLimitUpDownService.countByType(type);
        if (null == totalCount) {
            return Result.fail(ReturnCode.FAIL);
        }
        return Result.success(totalCount);
    }
}
