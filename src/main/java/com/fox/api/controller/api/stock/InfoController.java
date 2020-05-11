package com.fox.api.controller.api.stock;

import com.fox.api.entity.dto.result.ResultDto;
import com.fox.api.entity.vo.stock.StockVo;
import com.fox.api.entity.vo.stock.offline.StockOfflineLineVo;
import com.fox.api.service.stock.StockFollowService;
import com.fox.api.service.stock.StockInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@CrossOrigin(origins = "*")//处理跨域问题
@RestController
public class InfoController {

    @Autowired
    StockInfoService stockInfoService;

    /**
     * 获取股票基本信息
     * @param stockVo
     * @return
     */
    @RequestMapping("/stock/info/base")
    public ResultDto base(@Valid StockVo stockVo) {
        return ResultDto.success(stockInfoService.getInfo(stockVo.getStockId()));
    }

    /**
     * 搜索股票
     * @param search
     * @return
     */
    @RequestMapping("/stock/info/search")
    public ResultDto search(String search) {
        return ResultDto.success(stockInfoService.search(search));
    }
}
