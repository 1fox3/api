package com.fox.api.enums.code;

import com.fox.api.enums.code.CodeMsgEnum;

/**
 * 业务错误代码
 */
public enum LoginCode implements CodeMsgEnum {
    LOGIN_FAIL(1001, "登录失败");

    private Integer code;

    private String msg;

    LoginCode(int code, String msg) {
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
