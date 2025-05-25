package com.ycbd.demo.service.validator;

import cn.hutool.core.map.MapUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.ycbd.demo.mapper.SystemMapper;
import java.util.*;

/**
 * 规则验证器
 * 用于验证数据是否符合预定义的业务规则
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RuleValidator {
    
    // 系统映射器,用于数据库操作
    private final SystemMapper systemMapper;
    
    /**
     * 验证数据是否符合规则
     * @param table 目标表名
     * @param data 待验证的数据
     * @return 错误信息列表,如果为空则表示验证通过
     */
    public List<String> validate(String table, Map<String, Object> data) {
        // 存储错误信息
        List<String> errors = new ArrayList<>();
        // 获取表的所有校验规则
        List<Map<String, Object>> rules = systemMapper.findColumnCheckRules(table);
        
        // 遍历每个规则进行验证
        for (Map<String, Object> rule : rules) {
            try {
                // 获取规则相关参数
                String mode = MapUtil.getStr(rule, "check_mode");        // 校验模式
                String column = MapUtil.getStr(rule, "check_column");    // 校验字段
                String errorMsg = MapUtil.getStr(rule, "errorMsg");      // 错误信息
                String targetTable = MapUtil.getStr(rule, "target_table"); // 目标表名

                // 根据不同的校验模式执行相应的验证
                switch (mode) {
                    case "isExit":     // 存在性验证
                    case "isNotExit":  // 不存在性验证
                        validateExistence(targetTable, column, data, rule, mode, errorMsg, errors);
                        break;
                    case "MutiReapeat": // 多字段重复验证
                        validateMultipleFields(targetTable, column, data, errorMsg, errors);
                        break;
                    case "isRang":      // 范围验证
                        validateRange(column, data, errorMsg, errors);
                        break;
                    default:
                        log.warn("未知的校验规则类型: {}", mode);
                }
            } catch (Exception e) {
                // 记录异常但继续执行其他规则
                String errorDetail = String.format("规则校验异常[%s]: %s", 
                    MapUtil.getStr(rule, "check_mode"), e.getMessage());
                log.error(errorDetail, e);
                errors.add(errorDetail);
            }
        }
        
        return errors;
    }
    
    /**
     * 验证数据的存在性
     * @param table 目标表名
     * @param column 校验字段
     * @param data 待验证数据
     * @param rule 验证规则
     * @param mode 验证模式(isExit/isNotExit)
     * @param errorMsg 错误信息
     * @param errors 错误信息列表
     */
    private void validateExistence(String table, String column, Map<String, Object> data, Map<String, Object> rule, 
                                 String mode, String errorMsg, List<String> errors) {
        try {
            // 处理额外的where条件
            String whereStr = MapUtil.getStr(rule, "whereStr");
            String value = MapUtil.getStr(data, column);
            whereStr = whereStr.replaceFirst("%s", "'" + value + "'"); // 替换为字符串
            // 统计符合条件的记录数
            int count = systemMapper.countByWhere(table, whereStr);

            // 根据验证模式判断结果
            if ("isExit".equals(mode) && count == 0) {
                // 应该存在但不存在
                String error = String.format("数据不存在[%s.%s]: %s, 提供的值: %s", table, column, errorMsg, value);
                errors.add(error);
                log.warn(error);
            }

            if ("isNotExit".equals(mode) && count > 0) {
                // 不应该存在但存在
                String error = String.format("数据已存在[%s.%s]: %s, 提供的值: %s", table, column, errorMsg, value);
                errors.add(error);
                log.warn(error);
            }
        } catch (Exception e) {
            String errorDetail = String.format("存在性校验异常[%s.%s]: %s", table, column, e.getMessage());
            log.error(errorDetail, e);
            errors.add(errorDetail);
        }
    }
    
    /**
     * 验证多个字段是否重复
     * @param table 目标表名
     * @param columnName 字段名列表(逗号分隔)
     * @param data 待验证数据
     * @param errorMsg 错误信息
     * @param errors 错误信息列表
     */
    private void validateMultipleFields(String table, String columnName, 
            Map<String, Object> data, String errorMsg, List<String> errors) {
        try {
            // 分割字段名
            String[] columns = columnName.split(",");
            Set<Object> values = new HashSet<>();
            
            // 收集所有字段的值
            for (String column : columns) {
                Object value = data.get(column.trim());
                if (value != null) {
                    values.add(value);
                }
            }

            // 如果去重后的值数量小于字段数,说明有重复值
            if (values.size() < columns.length) {
                String error = String.format("字段重复[%s]: %s", columnName, errorMsg);
                errors.add(error);
                log.warn(error);
            }
        } catch (Exception e) {
            String errorDetail = String.format("多字段重复校验异常[%s]: %s", columnName, e.getMessage());
            log.error(errorDetail, e);
            errors.add(errorDetail);
        }
    }
    
    /**
     * 验证字段值是否在指定范围内
     * @param columnName 字段名
     * @param data 待验证数据
     * @param errorMsg 错误信息
     * @param errors 错误信息列表
     */
    private void validateRange(String columnName, Map<String, Object> data, String errorMsg, List<String> errors) {
        try {
            Object value = data.get(columnName);
            // 只验证数字类型的值
            if (value instanceof Number) {
                double numericValue = ((Number) value).doubleValue();
                // 验证值是否在18到60之间
                if (numericValue < 18 || numericValue > 60) {
                    String error = String.format("字段 %s 超出范围: %s", columnName, errorMsg);
                    errors.add(error);
                    log.warn(error);
                }
            }
        } catch (Exception e) {
            String errorDetail = String.format("范围校验异常[%s]: %s", columnName, e.getMessage());
            log.error(errorDetail, e);
            errors.add(errorDetail);
        }
    }
} 