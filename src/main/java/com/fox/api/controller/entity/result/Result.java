package com.fox.api.controller.entity.result;

import com.fox.api.controller.enums.code.ReturnCode;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    public static <T> Result<T> success(T body) {
        return new Result(ReturnCode.SUCCESS.getCode(), ReturnCode.SUCCESS.getMsg(), body);
    }

    public static Result success() {
        return new Result(ReturnCode.SUCCESS.getCode(), ReturnCode.SUCCESS.getMsg(), null);
    }

    public static <T> Result<T> fail(ReturnCode failCode, T body) {
        return new Result(failCode.getCode(), failCode.getMsg(), body);
    }

    public static Result fail(ReturnCode failCode) {
        return new Result(failCode.getCode(), failCode.getMsg(), null);
    }
}
