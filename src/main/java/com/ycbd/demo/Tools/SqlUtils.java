package com.ycbd.demo.Tools;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL工具类
 */
public class SqlUtils {
    
    /**
     * 处理SQL参数占位符
     * 将:param形式的占位符转换为#{param}形式
     */
    public static String processParamPlaceholders(String sql, Map<String, Object> params) {
        if (sql == null || params == null) {
            return sql;
        }
        
        Pattern pattern = Pattern.compile(":(\\w+)");
        Matcher matcher = pattern.matcher(sql);
        StringBuffer result = new StringBuffer();
        
        while (matcher.find()) {
            String paramName = matcher.group(1);
            if (params.containsKey(paramName)) {
                matcher.appendReplacement(result, "#{params." + paramName + "}");
            }
        }
        matcher.appendTail(result);
        
        return result.toString();
    }
} 