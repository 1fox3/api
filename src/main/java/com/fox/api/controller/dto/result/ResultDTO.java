package com.fox.api.controller.dto.result;

import com.fox.api.controller.enums.code.CodeMsgEnum;
import com.fox.api.controller.enums.code.ReturnCode;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResultDTO<T> {
    private int code;
    private String msg;
    private T data;

    public static <T> ResultDTO<T> success(T body) {
        return new ResultDTO(ReturnCode.SUCCESS.getCode(), ReturnCode.SUCCESS.getMsg(), body);
    }

    public static ResultDTO success() {
        return new ResultDTO(ReturnCode.SUCCESS.getCode(), ReturnCode.SUCCESS.getMsg(), null);
    }

    public static <T> ResultDTO<T> fail(CodeMsgEnum Obj, T body) {
        return new ResultDTO(Obj.getCode(), Obj.getMsg(), body);
    }

    public static <T> ResultDTO fail(CodeMsgEnum Obj) {
        return new ResultDTO(Obj.getCode(), Obj.getMsg(), null);
    }
}
