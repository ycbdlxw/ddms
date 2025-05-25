package com.ycbd.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统日志实体
 * @author your-name
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemLog {
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 请求路径
     */
    private String path;
    
    /**
     * 请求方法
     */
    private String method;
    
    /**
     * 请求参数
     */
    private String params;
    
    /**
     * 操作用户ID
     */
    private Integer userId;
    
    /**
     * 执行时长(毫秒)
     */
    private Long duration;
    
    /**
     * 响应状态码
     */
    private Integer status;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 创建时间
     */
    private Long createTime;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 操作类型
     */
    private String operationType;
    
    /**
     * 详情
     */
    private String detail;
    
    /**
     * 请求追踪ID
     */
    private String traceId;
} 