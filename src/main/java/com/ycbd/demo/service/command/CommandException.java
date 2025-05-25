package com.ycbd.demo.service.command;

/**
 * 命令执行异常基类
 */
public class CommandException extends RuntimeException {
    
    private final String command;
    private final int exitCode;
    
    public CommandException(String message, String command) {
        this(message, command, -1, null);
    }
    
    public CommandException(String message, String command, Throwable cause) {
        this(message, command, -1, cause);
    }
    
    public CommandException(String message, String command, int exitCode) {
        this(message, command, exitCode, null);
    }
    
    public CommandException(String message, String command, int exitCode, Throwable cause) {
        super(String.format("%s [命令: %s, 退出码: %d]", message, command, exitCode), cause);
        this.command = command;
        this.exitCode = exitCode;
    }
    
    public String getCommand() {
        return command;
    }
    
    public int getExitCode() {
        return exitCode;
    }
} 