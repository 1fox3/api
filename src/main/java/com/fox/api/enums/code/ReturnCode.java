package com.fox.api.enums.code;

/**
 * 业务错误代码
 * @author lusongsong
 */
public enum ReturnCode implements CodeMsgEnum {
    /**
     * 请求成功
     */
    SUCCESS(0, "成功"),
    /**
     * 请求失败
     */
    FAIL(1, "失败"),
    /**
     * 请求出现异常
     */
    CONTROLLER_EXCEPTION(1000, "请求出现异常"),
    /**
     * 参数格式错误
     */
    PARAM_FORMAT_ERROR(1001, "参数格式错误"),
    /**
     * 参数校验失败
     */
    PARAM_VERIFY_ERROR(1002, "参数校验失败");

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
    private CodeScopeEnum codeScopeEnum = CodeScopeEnum.REQUEST_CODE_SCOPE;

    /**
     * 构造函数，初始化错误码和错误信息
     * @param code
     * @param msg
     */
    ReturnCode(int code, String msg) {
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
