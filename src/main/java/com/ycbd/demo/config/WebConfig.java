package com.ycbd.demo.config;

import com.ycbd.demo.interceptor.ServiceInterceptorAspect;

import lombok.RequiredArgsConstructor;

import jakarta.annotation.Resource;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private ServiceInterceptorAspect serviceInterceptorAspect;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(serviceInterceptorAspect)
            .addPathPatterns("/api/**");  // 拦截所有 /api 路径
            
    }
}
