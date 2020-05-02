package com.fox.api.enums.code;

/**
 * 错误类
 * @author lusongsong
 */
public interface CodeMsgEnum {
    /**
     * 获取错误码
     * @return
     */
    Integer getCode();

    /**
     * 获取错误信息
     * @return
     */
    String getMsg();
}
