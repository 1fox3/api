package com.fox.api.entity.dto.result;

import com.fox.api.enums.code.CodeMsgEnum;
import com.fox.api.enums.code.ReturnCode;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 接口返回对象
 * @param <T>
 * @author lusongsong
 * @date 2020/3/5 18:13
 */
@Data
@AllArgsConstructor
public class ResultDto<T> {
    /**
     * 错误码
     */
    Integer code;
    /**
     * 错误信息
     */
    String msg;
    /**
     * 响应数据
     */
    T data;

    /**
     * 成功
     * @param body
     * @param <T>
     * @return
     */
    public static <T> ResultDto<T> success(T body) {
        return new ResultDto(ReturnCode.SUCCESS.getCode(), ReturnCode.SUCCESS.getMsg(), body);
    }

    /**
     * 成功
     * @return
     */
    public static ResultDto success() {
        return new ResultDto(ReturnCode.SUCCESS.getCode(), ReturnCode.SUCCESS.getMsg(), null);
    }

    /**
     * 失败
     * @param Obj
     * @param body
     * @param <T>
     * @return
     */
    public static <T> ResultDto<T> fail(CodeMsgEnum Obj, T body) {
        return new ResultDto(Obj.getCode(), Obj.getMsg(), body);
    }

    /**
     * 失败
     * @param Obj
     * @param <T>
     * @return
     */
    public static <T> ResultDto fail(CodeMsgEnum Obj) {
        return new ResultDto(Obj.getCode(), Obj.getMsg(), null);
    }

    /**
     * 失败
     * @param code
     * @param msg
     * @param <T>
     * @return
     */
    public static <T> ResultDto fail(Integer code, String msg) {
        return new ResultDto(code, msg, null);
    }

    /**
     * 失败
     * @param code
     * @param msg
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ResultDto fail(Integer code, String msg, Object data) {
        return new ResultDto(code, msg, data);
    }
}
