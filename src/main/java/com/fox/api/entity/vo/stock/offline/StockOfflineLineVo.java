package com.fox.api.entity.vo.stock.offline;

import com.fox.api.entity.vo.stock.StockVo;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 股票历史交易线图清请求参数
 * @author lusongsong
 * @date 2020/4/9 14:34
 */
@Data
public class StockOfflineLineVo extends StockVo {
    @NotBlank(message = "起始日期不能为空")
    @Pattern(regexp = "^\\d{4}-\\d{1,2}-\\d{1,2}$", message = "起始日期格式错误")
    private String startDate;

    @NotBlank(message = "截止日期不能为空")
    @Pattern(regexp = "^\\d{4}-\\d{1,2}-\\d{1,2}$", message = "截止日期格式错误")
    private String endDate;
}
