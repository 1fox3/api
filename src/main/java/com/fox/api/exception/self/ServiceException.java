package com.fox.api.exception.self;

import com.fox.api.enums.code.CodeMsgEnum;
import lombok.Data;

/**
 * 服务异常, 主要用于返回业务处理中出现的错误，方便提示使用方
 * @author lusongsong
 */
@Data
public class ServiceException extends RuntimeException {
    /**
     * 错误码
     */
    private Integer code;
    /**
     * 错误信息
     */
    private String msg;

    /**
     * 采用已知的错误码
     * @param codeMsgEnum
     */
    public ServiceException(CodeMsgEnum codeMsgEnum) {
        this.code = codeMsgEnum.getCode();
        this.msg = codeMsgEnum.getMsg();
    }

    /**
     * 采用位置的错误信息
     * @param code
     * @param msg
     */
    public ServiceException(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
