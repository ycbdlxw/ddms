package com.ycbd.demo.service.command;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * 命令工具类
 */
@Slf4j
public class CommandUtils {
    
    private static final Pattern WINDOWS_SPECIAL_CHARS = Pattern.compile("[\\s\"&<>|^]");
    private static final Pattern UNIX_SPECIAL_CHARS = Pattern.compile("[\\s\"'`\\\\$&<>|;(){}\\[\\]]");
    
    /**
     * 转义命令参数
     * @param arg 参数
     * @param isWindows 是否Windows系统
     * @return 转义后的参数
     */
    public static String escapeArgument(String arg, boolean isWindows) {
        if (arg == null || arg.isEmpty()) {
            return "\"\"";
        }
        
        Pattern pattern = isWindows ? WINDOWS_SPECIAL_CHARS : UNIX_SPECIAL_CHARS;
        if (!pattern.matcher(arg).find()) {
            return arg;
        }
        
        if (isWindows) {
            // Windows: 使用双引号包围,并转义内部的双引号
            return "\"" + arg.replace("\"", "\\\"") + "\"";
        } else {
            // Unix: 使用单引号包围,并转义内部的单引号
            return "'" + arg.replace("'", "'\\''") + "'";
        }
    }
    
    /**
     * 带重试的命令执行
     * @param operation 要执行的操作
     * @param maxAttempts 最大重试次数
     * @param initialDelay 初始延迟(毫秒)
     * @param maxDelay 最大延迟(毫秒)
     * @return 操作结果
     * @throws CommandException 如果所有重试都失败
     */
    public static <T> T withRetry(Supplier<T> operation, int maxAttempts, long initialDelay, long maxDelay) {
        int attempts = 0;
        long delay = initialDelay;
        Exception lastException = null;
        
        while (attempts < maxAttempts) {
            try {
                if (attempts > 0) {
                    log.debug("第{}次重试, 延迟{}毫秒", attempts, delay);
                    Thread.sleep(delay);
                }
                return operation.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new CommandException("命令执行被中断", "", e);
            } catch (Exception e) {
                lastException = e;
                attempts++;
                if (attempts < maxAttempts) {
                    // 指数退避策略
                    delay = Math.min(delay * 2, maxDelay);
                }
            }
        }
        
        if (lastException instanceof CommandException) {
            throw (CommandException) lastException;
        }
        throw new CommandException("命令执行失败,重试次数已用尽", "", lastException);
    }
    
    /**
     * 带超时的命令执行
     * @param operation 要执行的操作
     * @param timeout 超时时间
     * @param unit 时间单位
     * @return 操作结果
     * @throws CommandTimeoutException 如果执行超时
     */
    public static <T> T withTimeout(Supplier<T> operation, long timeout, TimeUnit unit) {
        Thread operationThread = new Thread(() -> {
            try {
                operation.get();
            } catch (Exception ignored) {
            }
        });
        
        operationThread.start();
        try {
            operationThread.join(unit.toMillis(timeout));
            if (operationThread.isAlive()) {
                operationThread.interrupt();
                throw new CommandTimeoutException("", timeout, unit);
            }
            return operation.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CommandException("命令执行被中断", "", e);
        }
    }
} 