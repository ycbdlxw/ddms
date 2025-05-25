package com.ycbd.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestExecutionResult {
    private List<CommandResult> commandResults = new ArrayList<>();
    private int totalCommands;
    private int successCount;
    private int failureCount;
    private String token;
    private String resultFilePath;
    
    public void addCommandResult(CommandResult result) {
        commandResults.add(result);
        totalCommands++;
        if (result.isSuccess()) {
            successCount++;
        } else {
            failureCount++;
        }
        if (result.getToken() != null) {
            this.token = result.getToken();
        }
    }
    
    public String getExecutionSummary() {
        return String.format("执行总结:\n" +
            "总命令数: %d\n" +
            "成功数: %d\n" +
            "失败数: %d\n", 
            totalCommands, successCount, failureCount);
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CommandResult {
        private String commandName;
        private String command;
        private boolean success;
        private String result;
        private String errorMessage;
        private String token;
        
        public CommandResult(String commandName, String command) {
            this.commandName = commandName;
            this.command = command;
        }
    }
} 