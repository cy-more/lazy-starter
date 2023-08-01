package com.lazy.web.support;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.lazy.entity.ResultMsg;
import com.lazy.exception.BizException;
import com.lazy.exception.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * @author ：cy
 * @description：错误码异常处理
 * 排除 ResponseEntityExceptionHandler
 * @date ：2022/3/14 10:03
 */
@Slf4j
@RestControllerAdvice
public class YsExceptionHandler {

    /**
     * 指定异常
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({BizException.class})
    public ResultMsg bizException(BizException e){
        return ResultCode.CONFIRM.getCode().equals(e.getErrorCode()) ?
                ResultMsg.fail(ResultCode.CONFIRM.getCode(), "", e.getMessage()) :
                ResultMsg.fail(e.getErrorCode(), e.getMessage());
    }

    /**
     * 参数异常
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({InvalidFormatException.class
            , IllegalArgumentException.class
            , BindException.class
            , HttpMessageNotReadableException.class
            , MethodArgumentNotValidException.class})
    public ResultMsg paramError(Exception e){
        log.info(e.getMessage(), e);
        String paramErrorCode = "10000";
        String message = e.getMessage();
        if (e instanceof BindException) {
            message = ((BindException)e).getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(","));
        }

        if (e instanceof HttpMessageNotReadableException) {
            message = "字段:" + e.getMessage().substring(e.getMessage().lastIndexOf("field :") + 7);
        }

        return ResultMsg.fail(paramErrorCode, "参数格式不正确:" + message);
    }

    /**
     * 权限不足
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(AccessDeniedException.class)
    public ResultMsg accessError(Exception e){
        log.info(e.getMessage(), e);
        return ResultMsg.fail(ResultCode.FORBIDDEN.getCode(), ResultCode.FORBIDDEN.getMessage());
    }

    /**
     * 响应结果
     * @param code
     * @param message
     * @return
     */
    protected ResponseEntity<ResultMsg> getMessage(String code, String message, HttpStatus status) {
        return new ResponseEntity<>(ResultMsg.fail(code, message), status);
    }
}
