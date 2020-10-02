package com.fox.api.entity.vo.stock;

import lombok.Data;

import javax.validation.constraints.Min;

/**
 * 股票基本信息
 * @author lusongsong
 * @date 2020/4/9 14:39
 */
@Data
public class StockVo {
    /**
     * 股票id
     */
    @Min(value = 1, message = "股票id不能小于1")
    Integer stockId;
}
