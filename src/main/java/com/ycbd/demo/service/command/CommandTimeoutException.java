package com.ycbd.demo.service.command;

import java.util.concurrent.TimeUnit;

/**
 * 命令执行超时异常
 */
public class CommandTimeoutException extends CommandException {
    
    private final long timeout;
    private final TimeUnit timeUnit;
    
    public CommandTimeoutException(String command, long timeout, TimeUnit timeUnit) {
        super(String.format("命令执行超时 [超时时间: %d %s]", timeout, timeUnit.name().toLowerCase()),
            command);
        this.timeout = timeout;
        this.timeUnit = timeUnit;
    }
    
    public long getTimeout() {
        return timeout;
    }
    
    public TimeUnit getTimeUnit() {
        return timeUnit;
    }
} 