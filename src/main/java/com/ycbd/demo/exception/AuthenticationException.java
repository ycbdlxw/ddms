package com.ycbd.demo.exception;

/**
 * 认证相关异常
 */
public class AuthenticationException extends RuntimeException {
    private final int status;
    
    public AuthenticationException(String message) {
        this(401, message);
    }
    
    public AuthenticationException(int status, String message) {
        super(message);
        this.status = status;
    }
    
    public int getStatus() {
        return status;
    }
} 