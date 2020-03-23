package com.fox.api.common.exception;

import com.fox.api.controller.dto.result.ResultDTO;
import com.fox.api.controller.enums.code.ReturnCode;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@ControllerAdvice
@RestController
public class ControllerExceptionHandler {
    //未特定处理的异常处理异常
    @ExceptionHandler(Exception.class)
    public ResultDTO exceptionHandler(Exception e) {
        return ResultDTO.fail(ReturnCode.PARAM_FORMAT_ERROR, e.getClass());
    }

    //参数格式不正确
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResultDTO httpMediaTypeNotSupportedExceptionHandler(HttpMediaTypeNotSupportedException e) {
        return ResultDTO.fail(ReturnCode.PARAM_FORMAT_ERROR, e.getMessage());
    }

    //参数不符合条件异常处理
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultDTO methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining());
        return ResultDTO.fail(ReturnCode.PARAM_VERIFY_ERROR, message);
    }
}
