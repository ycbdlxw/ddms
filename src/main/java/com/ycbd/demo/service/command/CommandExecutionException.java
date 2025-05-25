package com.ycbd.demo.service.command;

/**
 * 命令执行失败异常
 */
public class CommandExecutionException extends CommandException {
    
    private final String errorOutput;
    
    public CommandExecutionException(String command, int exitCode, String errorOutput) {
        super("命令执行失败", command, exitCode);
        this.errorOutput = errorOutput;
    }
    
    public CommandExecutionException(String command, int exitCode, String errorOutput, Throwable cause) {
        super("命令执行失败", command, exitCode, cause);
        this.errorOutput = errorOutput;
    }
    
    public String getErrorOutput() {
        return errorOutput;
    }
    
    @Override
    public String getMessage() {
        return String.format("%s [错误输出: %s]", super.getMessage(), errorOutput);
    }
} 