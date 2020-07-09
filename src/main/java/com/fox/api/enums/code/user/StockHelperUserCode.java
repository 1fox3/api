package com.fox.api.enums.code.user;

import com.fox.api.enums.code.CodeMsgEnum;
import com.fox.api.enums.code.CodeScopeEnum;

/**
 * 股票助手用户错误信息
 * @author lusongsong
 */
public enum StockHelperUserCode implements CodeMsgEnum {
    /**
     * 错误枚举值
     */
    ACCOUNT_ERROR(1,"账号错误"),
    VERIFY_CODE_ERROR(2,"验证码错误");

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
    private CodeScopeEnum codeScopeEnum = CodeScopeEnum.STOCK_HELPER_USER_SCOPE;

    /**
     * 构造函数，初始化错误码和错误信息
     * @param code
     * @param msg
     */
    StockHelperUserCode(int code, String msg) {
        this.code = codeScopeEnum.getCode() + code;
        this.msg = msg;
    }
    /**
     * 获取错误码
     *
     * @return
     */
    @Override
    public Integer getCode() {
        return null;
    }

    /**
     * 获取错误信息
     *
     * @return
     */
    @Override
    public String getMsg() {
        return null;
    }
}
