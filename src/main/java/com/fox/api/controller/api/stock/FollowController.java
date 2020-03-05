package com.fox.api.controller.api.stock;

import com.fox.api.controller.entity.result.Result;
import com.fox.api.service.stock.StockFollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")//处理跨域问题
@RestController
public class FollowController {

    @Autowired
    StockFollowService stockFollowService;

    @RequestMapping("/stock/follow/list")
    public Result realtime() {
        return Result.success(stockFollowService.getByUser(0));
    }
}
