package com.ycbd.demo.service.command;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.RuntimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Windows系统命令执行器
 */
@Slf4j
@Component
public class WindowsCommandExecutor implements CommandExecutor {
    
    private static final int DEFAULT_TIMEOUT = 30;
    private static final int MAX_RETRIES = 3;
    private static final long INITIAL_RETRY_DELAY = 1000;
    private static final long MAX_RETRY_DELAY = 5000;
    
    @Override
    public int execute(String command) throws CommandException {
        return CommandUtils.withRetry(() -> {
            try {
                log.debug("执行Windows命令: {}", command);
                String[] commandArray = {"cmd", "/c", command};
                Process process = RuntimeUtil.exec(commandArray);
                
                boolean completed = process.waitFor(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
                if (!completed) {
                    process.destroyForcibly();
                    throw new CommandTimeoutException(command, DEFAULT_TIMEOUT, TimeUnit.SECONDS);
                }
                
                int exitCode = process.exitValue();
                if (exitCode != 0) {
                    String errorOutput = IoUtil.read(process.getErrorStream(), StandardCharsets.UTF_8);
                    throw new CommandExecutionException(command, exitCode, errorOutput);
                }
                
                return exitCode;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new CommandException("命令执行被中断", command, e);
            }
        }, MAX_RETRIES, INITIAL_RETRY_DELAY, MAX_RETRY_DELAY);
    }
    
    @Override
    public String executeAndGetOutput(String command) throws CommandException {
        return CommandUtils.withRetry(() -> {
            try {
                log.debug("执行Windows命令并获取输出: {}", command);
                String[] commandArray = {"cmd", "/c", command};
                Process process = RuntimeUtil.exec(commandArray);
                
                try (InputStream inputStream = process.getInputStream()) {
                    boolean completed = process.waitFor(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
                    if (!completed) {
                        process.destroyForcibly();
                        throw new CommandTimeoutException(command, DEFAULT_TIMEOUT, TimeUnit.SECONDS);
                    }
                    
                    if (process.exitValue() != 0) {
                        String errorOutput = IoUtil.read(process.getErrorStream(), StandardCharsets.UTF_8);
                        throw new CommandExecutionException(command, process.exitValue(), errorOutput);
                    }
                    
                    return IoUtil.read(inputStream, StandardCharsets.UTF_8);
                }
            } catch (IOException | InterruptedException e) {
                if (e instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
                throw new CommandException("命令执行失败", command, e);
            }
        }, MAX_RETRIES, INITIAL_RETRY_DELAY, MAX_RETRY_DELAY);
    }
    
    @Override
    public boolean isSupported() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }
    
    /**
     * 转义命令参数
     */
    public String escapeArgument(String arg) {
        return CommandUtils.escapeArgument(arg, true);
    }
} 