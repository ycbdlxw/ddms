package com.ycbd.demo.config;

import com.ycbd.demo.interceptor.LogoutInterceptor;
import com.ycbd.demo.interceptor.ServiceInterceptorAspect;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final ServiceInterceptorAspect serviceInterceptor;
    private final LogoutInterceptor logoutInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册服务拦截器
        registry.addInterceptor(serviceInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/common/login", "/api/common/register");
        
        // 注册注销拦截器
        registry.addInterceptor(logoutInterceptor)
                .addPathPatterns("/api/common/logout");
    }
} 