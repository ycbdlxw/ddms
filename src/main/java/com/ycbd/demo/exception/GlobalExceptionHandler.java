package com.ycbd.demo.exception;

import com.ycbd.demo.Tools.ResultData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 处理认证相关异常
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResultData<?> handleAuthenticationException(AuthenticationException e) {
        log.warn("认证异常: {}", e.getMessage());
        return ResultData.fail(e.getStatus(), e.getMessage());
    }
    
    /**
     * 处理其他未知异常
     */
    @ExceptionHandler(Exception.class)
    public ResultData<?> handleException(Exception e) {
        log.error("系统异常", e);
        return ResultData.fail(500, "系统异常,请稍后重试");
    }
} 