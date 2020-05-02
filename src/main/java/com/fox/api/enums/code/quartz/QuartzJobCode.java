package com.fox.api.enums.code.quartz;

import com.fox.api.enums.code.CodeMsgEnum;
import com.fox.api.enums.code.CodeScopeEnum;

/**
 * 计划任务管理相关错误码
 * @author lusongsong
 */
public enum QuartzJobCode implements CodeMsgEnum {
    /**
     * 任务信息为空
     */
    QUARTZ_JOB_NOT_FOUND(1, "任务信息为空"),
    /**
     * 任务信息不允许删除
     */
    QUARTZ_JOB_DENY_DELETE(2, "任务信息不允许删除"),
    /**
     * Bean不存在
     */
    BEAN_NOT_FOUND(3, "Bean不存在"),
    /**
     * 方法不存在
     */
    METHOD_NOT_FOUND(4, "方法不存在"),
    /**
     * 方法与参数类型不匹配
     */
    METHOD_NOT_MATCH(5, "方法与参数类型不匹配");

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
    private CodeScopeEnum codeScopeEnum = CodeScopeEnum.QUARTZ_CODE_SCOPE;

    /**
     * 构造函数，初始化错误码和错误信息
     * @param code
     * @param msg
     */
    QuartzJobCode(int code, String msg) {
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
