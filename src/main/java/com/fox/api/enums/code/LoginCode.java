package com.fox.api.enums.code;

/**
 * 登录业务错误代码
 * @author lusongsong
 */
public enum LoginCode implements CodeMsgEnum {
    /**
     * 登录业务错误代码
     */
    LOGIN_FAIL(1, "登录失败");

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误信息
     */
    private String msg;

    /**
     * 错误号码段
     */
    private CodeScopeEnum codeScopeEnum = CodeScopeEnum.LOGIN_CODE_SCOPE;

    /**
     * 构造函数，初始化错误码和错误信息
     * @param code
     * @param msg
     */
    LoginCode(int code, String msg) {
        this.code = codeScopeEnum.getCode() + code;
        this.msg = msg;
    }

    /**
     * 获取错误码
     * @return
     */
    @Override
    public Integer getCode() {
        return code;
    }

    /**
     * 获取错误信息
     * @return
     */
    @Override
    public String getMsg() {
        return msg;
    }
}
