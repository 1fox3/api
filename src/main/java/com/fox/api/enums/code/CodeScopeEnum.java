package com.fox.api.enums.code;

/**
 * 错误码号段分布
 * @author lusongsong
 */
public enum CodeScopeEnum {
    /**
     * 请求相关
     */
    REQUEST_CODE_SCOPE(0),
    /**
     * 登录相关业务
     */
    LOGIN_CODE_SCOPE(1),
    /**
     * 计划任务管理
     */
    QUARTZ_CODE_SCOPE(2);

    /**
     * 号码段
     */
    private Integer code;
    /**
     * 每个业务预分配1000个错误码
     */
    private Integer scope = 1000;

    /**
     * 构造函数，初始化错误码号段
     * @param code
     */
    CodeScopeEnum(Integer code) {
        this.code = code * scope;
    }

    /**
     * 获取号码段
     * @return
     */
    public Integer getCode() {
        return code;
    }
}
