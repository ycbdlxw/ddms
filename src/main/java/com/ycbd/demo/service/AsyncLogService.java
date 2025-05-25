package com.ycbd.demo.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.ycbd.demo.model.SystemLog;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AsyncLogService {
    
    private final CommonService systemService;
    
    public AsyncLogService(CommonService systemService) {
        this.systemService = systemService;
    }
    
    @Async
    public void saveLog(SystemLog systemLog) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("targetTable", "sys_log");
            params.put("path", systemLog.getPath());
            params.put("method", systemLog.getMethod());
            params.put("params", systemLog.getParams());
            params.put("user_id", systemLog.getUserId());
            params.put("duration", systemLog.getDuration());
            params.put("status", systemLog.getStatus());
            params.put("error_message", systemLog.getErrorMessage());
            params.put("create_time", systemLog.getCreateTime());
            systemService.saveData(params);
        } catch (Exception e) {
            log.error("保存系统日志失败", e);
        }
    }
} 