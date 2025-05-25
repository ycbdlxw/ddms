package com.ycbd.demo.service.validator;

import cn.hutool.core.map.MapUtil;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.regex.Pattern;

@Component
public class AttributeValidator {
    
    public List<String> validate(List<Map<String, Object>> attributes, Map<String, Object> data) {
        List<String> errors = new ArrayList<>();
        
        for (Map<String, Object> attr : attributes) {
            String name = MapUtil.getStr(attr, "name");
            int required = MapUtil.getInt(attr, "IsRequired", 0);
            String fieldType = MapUtil.getStr(attr, "fieldType");
            int len = MapUtil.getInt(attr, "len", 0);
            String params = MapUtil.getStr(attr, "params", null); // 获取范围参数
            
            Object value = data.get(name);
            
            // 必填检查
            if (required == 1 && (value == null || value.toString().trim().isEmpty())) {
                errors.add(String.format("字段 %s 为必填项，不能为空", name));
                continue;
            }
            
            // 类型检查
            if (value != null && !value.toString().trim().isEmpty()) {
                validateFieldType(name, value.toString(), fieldType, len, params, errors);
            }
        }
        
        return errors;
    }
    
    private void validateFieldType(String name, String value, String fieldType, int len, String params, List<String> errors) {
        switch (fieldType.toLowerCase()) {
            case "datetime":
            case "date":
                if (!isValidDate(value)) {
                    errors.add(String.format("字段 %s 非日期时间字符: 提供的值 %s", name, value));
                }
                break;
            case "int":
            case "bigint":
            case "tinyint":
            case "smallint":
                if (!isValidInteger(value)) {
                    errors.add(String.format("字段 %s 非数字字符: 提供的值 %s", name, value));
                }
                break;
            case "decimal":
            case "double":
                if (!isValidDecimal(value)) {
                    errors.add(String.format("字段 %s 非小数字符: 提供的值 %s", name, value));
                }
                break;
            case "varchar":
                if (len > 0 && value.length() > len) {
                    errors.add(String.format("字段 %s 长度超出限制 %d: 提供的值 %s", name, len, value));
                }
                break;
            case "email":
                if (!isValidEmail(value)) {
                    errors.add(String.format("字段 %s 非有效邮箱: 提供的值 %s", name, value));
                }
                break;
            case "phone":
                if (!isValidPhone(value)) {
                    errors.add(String.format("字段 %s 非有效手机号码: 提供的值 %s", name, value));
                }
                break;
            case "url":
                if (!isValidUrl(value)) {
                    errors.add(String.format("字段 %s 非有效网址: 提供的值 %s", name, value));
                }
                break;
            case "file_extension":
                if (!isValidFileExtension(value)) {
                    errors.add(String.format("字段 %s 非有效文件扩展名: 提供的值 %s", name, value));
                }
                break;
            case "range":
                validateRange(name, value, params, errors);
                break;
        }
    }

    private void validateRange(String name, Object value, String params, List<String> errors) {
        if (params != null) {
            String[] range = params.split(",");
            if (range.length == 2) {
                double min = Double.parseDouble(range[0].trim());
                double max = Double.parseDouble(range[1].trim());
                double numericValue = ((Number) value).doubleValue();
                if (numericValue < min || numericValue > max) {
                    errors.add(String.format("字段 %s 超出范围: 提供的值 %s, 范围: [%s, %s]", name, value, min, max));
                }
            }
        }
    }

    private boolean isValidEmail(String value) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return Pattern.matches(emailRegex, value);
    }

    private boolean isValidPhone(String value) {
        String phoneRegex = "^\\+?[0-9]{10,15}$"; // 根据需要调整手机号码格式
        return Pattern.matches(phoneRegex, value);
    }

    private boolean isValidUrl(String value) {
        String urlRegex = "^(http|https)://.*$"; // 简单的 URL 校验
        return Pattern.matches(urlRegex, value);
    }

    private boolean isValidFileExtension(String value) {
        // 假设文件扩展名以 . 开头
        String fileExtensionRegex = "^[^.]+\\.[^.]+$"; // 简单的文件扩展名校验
        return Pattern.matches(fileExtensionRegex, value);
    }

    private boolean isValidDate(String value) {
        try {
            java.time.LocalDateTime.parse(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean isValidInteger(String value) {
        try {
            Long.parseLong(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private boolean isValidDecimal(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
} 