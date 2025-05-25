package com.ycbd.demo.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.core.collection.CollUtil;
import org.springframework.web.servlet.HandlerInterceptor;
import com.ycbd.demo.model.SystemLog;
import com.ycbd.demo.service.AsyncLogService;
import com.ycbd.demo.service.JwtService;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Set;

/**
 * 服务拦截切面
 * 负责请求的权限验证、日志记录等通用功能
 */
@Aspect
@Component
@Slf4j
public class ServiceInterceptorAspect implements HandlerInterceptor {
    
    @Resource
    private JwtService jwtService;
    
    @Resource
    private AsyncLogService asyncLogService;
    
    private static final ThreadLocal<Integer> currentUserId = new ThreadLocal<>();
    private static final ThreadLocal<String> currentOrgId = new ThreadLocal<>();
    private static final ThreadLocal<String> currentRoles = new ThreadLocal<>();
    private static final ThreadLocal<Long> startTime = new ThreadLocal<>();
    private static final ThreadLocal<String> traceId = new ThreadLocal<>();
    
    private static final Set<String> WHITE_LIST = CollUtil.newHashSet(
        "/api/common/login",
        "/api/common/register",
        "/api/common/captcha",
        "/api/test/run",
        "/swagger-ui/**",
        "/v3/api-docs/**"
    );
    
    /**
     * 清理所有ThreadLocal变量
     * 在finally块中调用,确保资源被正确释放
     */
    private void clearThreadLocals() {
        try {
            currentUserId.remove();
            currentOrgId.remove();
            currentRoles.remove();
            startTime.remove();
            traceId.remove();
        } catch (Exception e) {
            log.warn("清理ThreadLocal变量失败", e);
        }
    }
    
    /**
     * 请求预处理
     * 在Controller处理请求前执行
     * 主要负责:
     * 1. 记录请求开始时间
     * 2. 验证请求权限
     * 3. 解析JWT令牌
     * 4. 设置用户上下文信息
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            // 生成请求追踪ID
            String requestId = generateTraceId();
            traceId.set(requestId);
            log.info("[{}] 开始处理请求: {} {}", requestId, request.getMethod(), request.getRequestURI());

            startTime.set(System.currentTimeMillis());
            String path = request.getRequestURI();
            
            // 增加测试执行的详细日志
            if (path.startsWith("/api/test")) {
                log.info("[{}] 开始执行测试: {}", requestId, request.getQueryString());
            }
            
            // 白名单路径不校验token，但继续执行，以便记录日志
            if (isWhiteListPath(path)) {
                log.debug("[{}] 白名单路径: {}, 跳过token验证", requestId, path);
                return true;
            }
            
            // 获取并验证token
            String token = request.getHeader("Authorization");
            if (StrUtil.isBlank(token)) {
                log.warn("[{}] 未提供token", requestId);
                handleUnauthorized(response, "未登录");
                return false;
            }
            
            try {
                // 验证token并获取JWT对象
                JWT jwt = jwtService.validateToken(token);
                
                // 设置用户信息到ThreadLocal
                if(jwt.getPayload("userId")!=null)
                    currentUserId.set(Integer.valueOf(jwt.getPayload("userId").toString()));
                if(jwt.getPayload("orgId")!=null)
                    currentOrgId.set(jwt.getPayload("orgId").toString());
                if(jwt.getPayload("roles")!=null)
                    currentRoles.set(jwt.getPayload("roles").toString());
                log.debug("[{}] Token验证成功, 用户ID: {}", requestId, getCurrentUserId());
                
                return true;
            } catch (Exception e) {
                log.warn("[{}] Token验证失败: {}", requestId, e.getMessage());
                handleUnauthorized(response, e.getMessage());
                return false;
            }
        } catch (Exception e) {
            log.error("[{}] 请求预处理失败: {}", traceId.get(), e.getMessage(), e);
            clearThreadLocals();
            return false;
        }
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                              Object handler, Exception ex) {
        try {
            long duration = System.currentTimeMillis() - startTime.get();
            String path = request.getRequestURI();
            
            String requestId = traceId.get();
            log.info("[{}] 请求处理完成: {} {}, 耗时: {}ms, 状态: {}", 
                requestId, request.getMethod(), path, duration, response.getStatus());
            
            // 构建系统日志
            SystemLog.SystemLogBuilder logBuilder = SystemLog.builder()
                .path(path)
                .method(request.getMethod())
                .params(JSONUtil.toJsonStr(request.getParameterMap()))
                .userId(getCurrentUserId() != null ? getCurrentUserId() : 0)
                .duration(duration)
                .status(response.getStatus())
                .errorMessage(ex != null ? ex.getMessage() : null)
                .traceId(requestId)
                .createTime(System.currentTimeMillis());

            if (path.startsWith("/api/test")) {
                Object responseBody = request.getAttribute("responseBody");
                if (responseBody != null) {
                    logBuilder
                        .description("测试执行结果")
                        .operationType("TEST_EXECUTION")
                        .detail(JSONUtil.toJsonStr(responseBody));
                }
                
                if (ex != null) {
                    logBuilder
                        .description("测试执行失败")
                        .operationType("TEST_FAILURE");
                }
            }

            asyncLogService.saveLog(logBuilder.build());
            
        } finally {
            clearThreadLocals();
        }
    }
    
    private boolean isWhiteListPath(String path) {
        return WHITE_LIST.stream()
            .anyMatch(whitePath -> 
                whitePath.endsWith("/**") 
                    ? path.startsWith(whitePath.substring(0, whitePath.length() - 3))
                    : path.equals(whitePath)
            );
    }
    
    public static Integer getCurrentUserId() {
        return currentUserId.get();
    }
    
    public static String getCurrentOrgId() {
        return currentOrgId.get();
    }
    
    public static String getCurrentRoles() {
        return currentRoles.get();
    }

    /**
     * 生成请求追踪ID
     */
    private String generateTraceId() {
        return System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }
    
    /**
     * 处理未授权的请求
     */
    private void handleUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSONUtil.toJsonStr(new ErrorResponse(401, message)));
    }
    
    /**
     * 错误响应对象
     */
    private static class ErrorResponse {
        private final int code;
        private final String message;
        
        public ErrorResponse(int code, String message) {
            this.code = code;
            this.message = message;
        }
        
        @SuppressWarnings("unused")
        public int getCode() {
            return code;
        }
        
        @SuppressWarnings("unused")
        public String getMessage() {
            return message;
        }
    }
}
