package com.fox.api.controller.api.stock;

import com.fox.api.entity.dto.result.ResultDto;
import com.fox.api.enums.code.ReturnCode;
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
    public ResultDto list(String orderBy, PageInfo pageInfo) {
        orderBy = null == orderBy ? "d10_up DESC" : orderBy;
        List<StockUpDown> list = stockUpDownService.getList(orderBy, pageInfo);
        if (null == list) {
            return ResultDto.fail(ReturnCode.FAIL);
        }
        return ResultDto.success(list);
    }

    @RequestMapping("/stock/limitUpDown/list")
    public ResultDto list(Integer type, PageInfo pageInfo) {
        type = null == type ? 1 : type;
        List<StockLimitUpDown> list = stockLimitUpDownService.getList(type, pageInfo);
        if (null == list) {
            return ResultDto.fail(ReturnCode.FAIL);
        }
        return ResultDto.success(list);
    }

    @RequestMapping("/stock/limitUpDown/count")
    public ResultDto list(Integer type) {
        type = null == type ? 1 : type;
        Integer totalCount = stockLimitUpDownService.countByType(type);
        if (null == totalCount) {
            return ResultDto.fail(ReturnCode.FAIL);
        }
        return ResultDto.success(totalCount);
    }
}
