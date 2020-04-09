package com.fox.api.entity.vo.stock;

import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class StockVo {
    @Min(value = 1, message = "股票id不能小于1")
    private Integer stockId;
}
