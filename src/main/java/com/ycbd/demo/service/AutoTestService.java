package com.ycbd.demo.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.ycbd.demo.service.command.CommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ycbd.demo.model.TestExecutionResult;
import com.ycbd.demo.model.TestExecutionResult.CommandResult;

/**
 * 自动化测试服务类
 * 用于执行curl命令形式的HTTP接口测试脚本,并生成测试报告
 */

@Service
@RequiredArgsConstructor
public class AutoTestService {
    // 日志对象
    private static final Log log = LogFactory.get();
    // 系统文件分隔符
    private static final String FILE_SEPARATOR = File.separator;
    // 默认测试结果保存目录
    private static final String DEFAULT_RESULT_DIR = "test_results";
    
    private final CommandService commandService;
    
    /**
     * 执行测试脚本
     * @param scriptPath 脚本路径
     * @param resultDir 结果保存目录,如果为空则使用默认目录
     * @param useCurrentDir 是否使用脚本所在目录作为基准目录
     * @return 测试执行结果
     */
    public TestExecutionResult executeTests(String scriptPath, String resultDir, boolean useCurrentDir) {
        TestExecutionResult executionResult = new TestExecutionResult();
        
        try {
            log.info("开始执行测试，参数：scriptPath={}, resultDir={}, useCurrentDir={}", scriptPath, resultDir, useCurrentDir);
            
            // 处理脚本路径
            File scriptFile;
            if (!new File(scriptPath).isAbsolute()) {
                // 如果是相对路径，则相对于项目根目录
                String userDir = System.getProperty("user.dir");
                scriptFile = new File(userDir, scriptPath);
            } else {
                scriptFile = new File(scriptPath);
            }
            
            if (!scriptFile.exists()) {
                throw new RuntimeException("脚本文件不存在: " + scriptFile.getAbsolutePath());
            }
            
            log.info("脚本文件绝对路径: {}", scriptFile.getAbsolutePath());
            
            // 确定结果保存目录
            String targetDir;
            if (useCurrentDir) {
                String baseDir = scriptFile.getParent();
                log.info("基准目录: {}", baseDir);
                
                // 如果提供了resultDir,将其作为相对路径添加到baseDir
                targetDir = resultDir != null && !resultDir.trim().isEmpty()
                    ? baseDir + FILE_SEPARATOR + resultDir.trim()
                    : baseDir + FILE_SEPARATOR + DEFAULT_RESULT_DIR;
            } else {
                // 使用原有逻辑
                targetDir = resultDir != null && !resultDir.trim().isEmpty()
                    ? resultDir.trim()
                    : DEFAULT_RESULT_DIR;
            }
            log.info("目标目录: {}", targetDir);
            
            String content;
            try {
                content = FileUtil.readString(scriptFile, StandardCharsets.UTF_8);
                log.info("成功读取脚本文件");
            } catch (Exception e) {
                log.error("读取脚本文件失败", e);
                throw new RuntimeException("读取脚本文件失败: " + e.getMessage());
            }
            
            List<String> commands = parseCurlCommands(content);
            log.info("解析出{}个测试命令", commands.size());
            
            String resultPath = targetDir + FILE_SEPARATOR + 
                              LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + "_result.txt";
            log.info("结果文件路径: {}", resultPath);
            
            // 创建目录(如果不存在)
            try {
                FileUtil.mkdir(targetDir);
                log.info("成功创建目录: {}", targetDir);
            } catch (Exception e) {
                log.error("创建目录失败", e);
                throw new RuntimeException("创建目录失败: " + e.getMessage());
            }
            
            // 用于构建最终的测试报告内容
            StringBuilder resultBuilder = new StringBuilder();
            int testCount = 0;
            
            // 依次执行每个curl命令
            for (String command : commands) {
                testCount++;
                String commandName = extractCommandName(command);
                CommandResult cmdResult = executeTestCommand(commandName, command, executionResult.getToken(), testCount, resultBuilder);
                executionResult.addCommandResult(cmdResult);
            }
            
            // 添加执行总结到测试报告
            resultBuilder.append("\n执行总结:\n");
            resultBuilder.append(executionResult.getExecutionSummary());
            
            // 将测试报告写入文件
            FileUtil.writeString(resultBuilder.toString(), resultPath, StandardCharsets.UTF_8);
            executionResult.setResultFilePath(resultPath);
            
            log.info("测试结果已保存到: {}", resultPath);
            
        } catch (Exception e) {
            log.error("执行测试失败", e);
            throw new RuntimeException("执行测试失败: " + e.getMessage(), e);
        }
        
        return executionResult;
    }
    
    /**
     * 从脚本内容中解析出所有curl命令
     * @param content 脚本文件内容
     * @return curl命令列表
     */
    private List<String> parseCurlCommands(String content) {
        List<String> commands = new ArrayList<>();
        // 使用正则表达式匹配curl命令
        Pattern pattern = Pattern.compile("curl.*?(?=curl|$)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);
        
        while (matcher.find()) {
            String command = matcher.group()
                    .replaceAll("\\\\\n\\s*", " ")  // 处理多行命令,将换行符替换为空格
                    .trim();
            commands.add(command);
        }
        return commands;
    }
    
    /**
     * 执行curl命令
     * @param command curl命令字符串
     * @param token 登录token
     * @return 命令执行结果
     */
    private String executeCommand(String command, String token) {
        try {
            log.debug("执行命令: {}", command);
            return commandService.executeAndGetOutput(command);
        } catch (Exception e) {
            log.error("命令执行异常: {}", e.getMessage());
            return "执行异常: " + e.getMessage();
        }
    }
    
    /**
     * 根据命令中的API路径识别命令类型
     * @param command curl命令
     * @return 命令类型描述
     */
    private String extractCommandName(String command) {
        if (command.contains("/api/common/login")) {
            return "登录测试";
        } else if (command.contains("/api/common/list")) {
            return "查询用户列表";
        } else if (command.contains("/api/common/save")) {
            return "新增用户";
        } else if (command.contains("/api/common/delete")) {
            return "删除用户";
        }
        return "未知命令";
    }

    /**
     * 从登录响应中提取token
     * @param response 登录响应字符串
     * @return token字符串,如果提取失败则返回null
     */
    private String extractToken(String response) {
        try {
            JSONObject jsonResponse = JSONUtil.parseObj(response);
            if (jsonResponse.getInt("code") == 200) {
                JSONObject result = jsonResponse.getJSONObject("result");
                if (result != null) {
                    return result.getStr("token");
                }
            }
            log.warn("登录响应中未找到有效token: {}", response);
        } catch (Exception e) {
            log.error("解析登录响应失败: {}", e.getMessage());
        }
        return null;
    }

    private void appendTestResult(StringBuilder resultBuilder, String testName, String command, String result) {
        resultBuilder.append("测试名称: ").append(testName).append("\n");
        resultBuilder.append("测试命令:\n").append(command).append("\n");
        resultBuilder.append("测试结果:\n").append(result).append("\n");
        resultBuilder.append("----------------------------------------\n\n");
    }

    /**
     * 执行单个测试命令
     */
    private CommandResult executeTestCommand(String commandName, String command, String token, int testCount, StringBuilder resultBuilder) {
        CommandResult cmdResult = new CommandResult();
        cmdResult.setCommandName(commandName);
        cmdResult.setCommand(command);
        
        try {
            // 处理登录请求
            if (commandName.contains("登录")) {
                String result = executeCommand(command, null);
                cmdResult.setResult(result);
                
                try {
                    JSONObject jsonResponse = JSONUtil.parseObj(result);
                    if (jsonResponse.containsKey("code")) {
                        int code = jsonResponse.getInt("code");
                        cmdResult.setSuccess(code == 200);
                        if (code != 200 && jsonResponse.containsKey("message")) {
                            cmdResult.setErrorMessage(jsonResponse.getStr("message"));
                        }
                    }
                    
                    if (cmdResult.isSuccess()) {
                        String newToken = extractToken(result);
                        if (newToken != null) {
                            cmdResult.setToken(newToken);
                            log.debug("成功获取token: {}", newToken);
                        }
                    }
                } catch (Exception e) {
                    log.warn("解析登录响应失败: {}", e.getMessage());
                    cmdResult.setSuccess(false);
                    cmdResult.setErrorMessage("解析响应失败: " + e.getMessage());
                }
                
                appendTestResult(resultBuilder,
                    String.format("测试 #%d: %s", testCount, commandName),
                    command, result);
            }
            // 处理其他需要token的接口
            else {
                if (command.contains("${TOKEN}")) {
                    if (token == null) {
                        throw new RuntimeException("Token未获取,无法执行需要认证的请求");
                    }
                    command = command.replace("${TOKEN}", token);
                    log.debug("已替换token");
                }
                
                String result = executeCommand(command, token);
                cmdResult.setResult(result);
                
                try {
                    JSONObject jsonResponse = JSONUtil.parseObj(result);
                    if (jsonResponse.containsKey("code")) {
                        int code = jsonResponse.getInt("code");
                        cmdResult.setSuccess(code == 200);
                        if (code != 200 && jsonResponse.containsKey("message")) {
                            cmdResult.setErrorMessage(jsonResponse.getStr("message"));
                        }
                    }
                } catch (Exception e) {
                    log.warn("解析响应失败: {}", e.getMessage());
                    cmdResult.setSuccess(false);
                    cmdResult.setErrorMessage("解析响应失败: " + e.getMessage());
                }
                
                appendTestResult(resultBuilder,
                    String.format("测试 #%d: %s", testCount, commandName),
                    command, result);
            }
        } catch (Exception e) {
            cmdResult.setSuccess(false);
            cmdResult.setErrorMessage(e.getMessage());
            log.error("执行命令失败: {}", commandName, e);
            appendTestResult(resultBuilder,
                String.format("测试 #%d: %s (失败)", testCount, commandName),
                command, "错误: " + e.getMessage());
        }
        
        return cmdResult;
    }
} 