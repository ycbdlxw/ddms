package com.ycbd.demo.service.validator;

import lombok.Getter;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidationResult {
    private final List<String> errors = new ArrayList<>();
    private final List<Exception> exceptions = new ArrayList<>();
    
    public void addError(String error) {
        errors.add(error);
    }
    
    public void addException(Exception e) {
        exceptions.add(e);
    }
    
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    public String getErrorMessage() {
        return String.join("; ", errors);
    }
    
    public String getFullErrorMessage() {
        StringBuilder sb = new StringBuilder();
        
        // 添加错误信息
        if (!errors.isEmpty()) {
            sb.append("验证错误: \n");
            errors.forEach(error -> sb.append("- ").append(error).append("\n"));
        }
        
        // 添加异常信息
        if (!exceptions.isEmpty()) {
            sb.append("系统异常: \n");
            exceptions.forEach(e -> sb.append("- ").append(e.getMessage()).append("\n"));
        }
        
        return sb.toString();
    }
} 