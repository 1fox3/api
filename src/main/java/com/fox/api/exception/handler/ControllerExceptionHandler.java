package com.fox.api.exception.handler;

import com.fox.api.entity.dto.result.ResultDto;
import com.fox.api.enums.code.ReturnCode;
import com.fox.api.exception.self.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

/**
 * controller类异常处理
 * @author lusongsong
 */
@ControllerAdvice
@RestController
public class ControllerExceptionHandler {
    /**
     * 未特定处理的异常处理异常
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResultDto exceptionHandler(Exception e) {
        return ResultDto.fail(ReturnCode.CONTROLLER_EXCEPTION, e.getClass());
    }

    /**
     * 具体某个参数格式不正确
     * @param e
     * @return
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResultDto illegalArgumentExceptionHandler(IllegalArgumentException e) {
        return ResultDto.fail(ReturnCode.PARAM_FORMAT_ERROR, e.getMessage());
    }

    /**
     * 具体某个参数格式不正确
     * @param e
     * @return
     */
    @ExceptionHandler(BindException.class)
    public ResultDto bindExceptionHandler(BindException e) {
        return ResultDto.fail(ReturnCode.PARAM_FORMAT_ERROR, e.getAllErrors().get(0).getDefaultMessage());
    }

    /**
     * 参数格式不正确
     * @param e
     * @return
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResultDto httpMediaTypeNotSupportedExceptionHandler(HttpMediaTypeNotSupportedException e) {
        return ResultDto.fail(ReturnCode.PARAM_FORMAT_ERROR, e.getMessage());
    }

    /**
     * 参数不符合条件异常处理
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultDto methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining());
        return ResultDto.fail(ReturnCode.PARAM_VERIFY_ERROR, message);
    }

    /**
     * 处理service处理异常
     * @param e
     * @return
     */
    @ExceptionHandler(ServiceException.class)
    public ResultDto serviceExceptionHandler(ServiceException e) {
        return ResultDto.fail(e.getCode(), e.getMsg());
    }
}
