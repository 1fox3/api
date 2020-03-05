package com.fox.api.controller.enums.code;

import lombok.Getter;
import lombok.Setter;

/**
 * 业务错误代码
 */
public enum ReturnCode {
    SUCCESS(0, "成功"),
    FAIL(1, "失败");

    @Getter
    @Setter
    private int code;

    @Getter
    @Setter
    private String msg;

    ReturnCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
