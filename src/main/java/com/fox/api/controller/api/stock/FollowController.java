package com.fox.api.controller.api.stock;

import com.fox.api.entity.dto.result.ResultDto;
import com.fox.api.service.stock.StockFollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 关注列表
 * @author lusongsong
 */
@CrossOrigin(origins = "*")//处理跨域问题
@RestController
public class FollowController {

    @Autowired
    StockFollowService stockFollowService;

    @RequestMapping("/stock/follow/list")
    public ResultDto realtime() {
        return ResultDto.success(stockFollowService.getByUser(0));
    }
}
