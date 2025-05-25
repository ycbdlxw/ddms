package com.ycbd.demo.service.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 命令执行服务
 * 管理和协调不同平台的命令执行器
 */
@Slf4j
@Service
public class CommandService {
    
    private final List<CommandExecutor> executors;
    private CommandExecutor currentExecutor;
    
    public CommandService(List<CommandExecutor> executors) {
        this.executors = executors;
        initializeExecutor();
    }
    
    /**
     * 初始化当前系统适用的命令执行器
     */
    private void initializeExecutor() {
        for (CommandExecutor executor : executors) {
            if (executor.isSupported()) {
                currentExecutor = executor;
                log.info("使用命令执行器: {}", executor.getClass().getSimpleName());
                return;
            }
        }
        throw new IllegalStateException("未找到支持当前操作系统的命令执行器");
    }
    
    /**
     * 执行命令并返回退出码
     */
    public int execute(String command) throws CommandException {
        log.debug("执行命令: {}", command);
        return currentExecutor.execute(command);
    }
    
    /**
     * 执行命令并返回输出结果
     */
    public String executeAndGetOutput(String command) throws CommandException {
        log.debug("执行命令并获取输出: {}", command);
        return currentExecutor.executeAndGetOutput(command);
    }
    
    /**
     * 判断指定目录是否已挂载
     */
    public boolean isMounted(String mountPoint) {
        try {
            String command;
            if (currentExecutor instanceof WindowsCommandExecutor) {
                command = "net use | findstr /i " + escapeArgument(mountPoint);
            } else {
                command = "mount | grep " + escapeArgument(mountPoint);
            }
            return execute(command) == 0;
        } catch (Exception e) {
            log.error("检查挂载状态失败: {}", mountPoint, e);
            return false;
        }
    }
    
    /**
     * 创建目录
     */
    public boolean createDirectory(String path) {
        try {
            String command;
            if (currentExecutor instanceof WindowsCommandExecutor) {
                command = String.format("mkdir \"%s\"", escapeArgument(path));
            } else {
                command = String.format("mkdir -p \"%s\"", escapeArgument(path));
            }
            return execute(command) == 0;
        } catch (Exception e) {
            log.error("创建目录失败: {}", path, e);
            return false;
        }
    }
    
    /**
     * 挂载远程目录
     */
    public boolean mountRemoteDirectory(String host, String remotePath, String mountPoint, 
            String username, String password) {
        try {
            String command;
            if (currentExecutor instanceof WindowsCommandExecutor) {
                command = String.format(
                    "net use %s \\\\%s%s /user:%s %s /PERSISTENT:YES",
                    escapeArgument(mountPoint),
                    escapeArgument(host),
                    escapeArgument(remotePath),
                    escapeArgument(username),
                    escapeArgument(password)
                );
            } else {
                command = String.format(
                    "mount -t cifs //%s%s %s -o username=%s,password=%s",
                    escapeArgument(host),
                    escapeArgument(remotePath),
                    escapeArgument(mountPoint),
                    escapeArgument(username),
                    escapeArgument(password)
                );
            }
            return execute(command) == 0;
        } catch (Exception e) {
            log.error("挂载远程目录失败: {} -> {}", host + remotePath, mountPoint, e);
            return false;
        }
    }
    
    /**
     * 卸载远程目录
     */
    public boolean unmountRemoteDirectory(String mountPoint) {
        try {
            String command;
            if (currentExecutor instanceof WindowsCommandExecutor) {
                command = String.format("net use %s /delete /y", escapeArgument(mountPoint));
            } else {
                command = String.format("umount %s", escapeArgument(mountPoint));
            }
            return execute(command) == 0;
        } catch (Exception e) {
            log.error("卸载远程目录失败: {}", mountPoint, e);
            return false;
        }
    }
    
    /**
     * 转义命令参数
     */
    private String escapeArgument(String arg) {
        return currentExecutor instanceof WindowsCommandExecutor ? 
            ((WindowsCommandExecutor) currentExecutor).escapeArgument(arg) :
            ((UnixCommandExecutor) currentExecutor).escapeArgument(arg);
    }
} 