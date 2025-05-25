package com.ycbd.demo.service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
@RequiredArgsConstructor
public class DataValidator {
    
    private final AttributeValidator attributeValidator;
    private final RuleValidator ruleValidator;
    
    public ValidationResult validate(String table, Map<String, Object> data, List<Map<String, Object>> attributes) {
        ValidationResult result = new ValidationResult();
        
        // 基本属性验证
        List<String> attributeErrors = attributeValidator.validate(attributes, data);
        result.getErrors().addAll(attributeErrors);
        
        // 规则验证
        List<String> ruleErrors = ruleValidator.validate(table, data);
        result.getErrors().addAll(ruleErrors);
        
        return result;
    }
} 