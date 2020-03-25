package com.fox.api.enums.code;

import com.fox.api.enums.code.CodeMsgEnum;

/**
 * 业务错误代码
 */
public enum ReturnCode implements CodeMsgEnum {
    SUCCESS(0, "成功"),
    FAIL(1, "失败"),
    PARAM_FORMAT_ERROR(1001, "参数格式错误"),
    PARAM_VERIFY_ERROR(1002, "参数校验失败");

    private int code;

    private String msg;

    ReturnCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
