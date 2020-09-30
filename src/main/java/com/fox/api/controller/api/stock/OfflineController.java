package com.fox.api.controller.api.stock;

import com.fox.api.entity.dto.result.ResultDto;
import com.fox.api.entity.dto.stock.offline.StockDealDayLineDto;
import com.fox.api.entity.po.third.stock.StockDayLinePo;
import com.fox.api.entity.po.third.stock.StockDealNumPo;
import com.fox.api.entity.vo.stock.offline.StockOfflineLineVo;
import com.fox.api.enums.code.ReturnCode;
import com.fox.api.service.stock.StockOfflineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 股票历史交易信息
 * @author lusongsong
 * @date 2020/5/3 18:13
 */
@CrossOrigin(origins = "*")
@RestController
public class OfflineController {
    @Autowired
    private StockOfflineService stockOfflineService;

    @RequestMapping("/stock/offline/line")
    public ResultDto line(@Valid StockOfflineLineVo stockOfflineLineVo) {
        StockDealDayLineDto stockDealDayLineDto = stockOfflineService.line(
                stockOfflineLineVo.getStockId(),
                stockOfflineLineVo.getStartDate(),
                stockOfflineLineVo.getEndDate()
        );
        if (null == stockDealDayLineDto) {
            return ResultDto.fail(ReturnCode.FAIL);
        }
        return ResultDto.success(stockDealDayLineDto);
    }

    @RequestMapping("/stock/offline/dealRatio")
    public ResultDto dealRatio(@Valid StockOfflineLineVo stockOfflineLineVo) {
        List<StockDealNumPo> list = stockOfflineService.dealRatio(
                stockOfflineLineVo.getStockId(),
                stockOfflineLineVo.getStartDate(),
                stockOfflineLineVo.getEndDate()
        );
        if (null == list) {
            return ResultDto.fail(ReturnCode.FAIL);
        }
        return ResultDto.success(list);
    }

    @RequestMapping("/stock/offline/day")
    public ResultDto day(Integer stockId, Integer fqType) {
        fqType = null == fqType ? 0 : fqType;
        return ResultDto.success(stockOfflineService.day(stockId, fqType));
    }
}
