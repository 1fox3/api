package com.fox.api.controller.vo;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class UserVo {
    @NotNull(message = "用户id不能缺失")
    @NotEmpty(message = "用户id不能为空")
    @Min(value = 1, message = "最小是1")
    private Integer userId;
}
