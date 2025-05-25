package com.ycbd.demo.service.command;

/**
 * 命令执行器接口
 * 定义了跨平台命令执行的基本方法
 */
public interface CommandExecutor {
    
    /**
     * 执行命令并返回退出码
     * @param command 要执行的命令
     * @return 命令的退出码,0表示成功
     * @throws CommandTimeoutException 执行超时
     * @throws CommandExecutionException 执行失败
     * @throws CommandException 其他错误
     */
    int execute(String command) throws CommandException;
    
    /**
     * 执行命令并返回输出结果
     * @param command 要执行的命令
     * @return 命令的输出结果
     * @throws CommandTimeoutException 执行超时
     * @throws CommandExecutionException 执行失败
     * @throws CommandException 其他错误
     */
    String executeAndGetOutput(String command) throws CommandException;
    
    /**
     * 判断当前执行器是否支持当前操作系统
     * @return 如果支持返回true,否则返回false
     */
    boolean isSupported();
} 