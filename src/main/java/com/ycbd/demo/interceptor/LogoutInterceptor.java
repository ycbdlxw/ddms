package com.ycbd.demo.interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 注销拦截器
 * 处理用户注销时的清理工作
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LogoutInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 只处理注销请求
        if (request.getRequestURI().equals("/api/common/logout")) {
            try {
                // 获取当前用户ID
                Integer userId = ServiceInterceptorAspect.getCurrentUserId();
                if (userId != null) {
                    log.info("用户[{}]注销成功", userId);
                }
            } catch (Exception e) {
                log.error("处理用户注销时发生错误", e);
            }
        }
        return true;
    }
} 