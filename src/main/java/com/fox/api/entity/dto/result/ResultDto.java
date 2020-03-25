package com.fox.api.entity.dto.result;

import com.fox.api.enums.code.CodeMsgEnum;
import com.fox.api.enums.code.ReturnCode;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResultDto<T> {
    private int code;
    private String msg;
    private T data;

    public static <T> ResultDto<T> success(T body) {
        return new ResultDto(ReturnCode.SUCCESS.getCode(), ReturnCode.SUCCESS.getMsg(), body);
    }

    public static ResultDto success() {
        return new ResultDto(ReturnCode.SUCCESS.getCode(), ReturnCode.SUCCESS.getMsg(), null);
    }

    public static <T> ResultDto<T> fail(CodeMsgEnum Obj, T body) {
        return new ResultDto(Obj.getCode(), Obj.getMsg(), body);
    }

    public static <T> ResultDto fail(CodeMsgEnum Obj) {
        return new ResultDto(Obj.getCode(), Obj.getMsg(), null);
    }
}
