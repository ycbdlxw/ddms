package com.ycbd.demo.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.JWTValidator;
import com.ycbd.demo.exception.AuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * JWT服务
 * 处理token的生成、验证、刷新等操作
 */
@Slf4j
@Service
public class JwtService {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expire-time}")
    private long expireTime;
    
    /**
     * 生成JWT token
     */
    public String generateToken(Map<String, Object> payload) {
        payload.put("exp", DateUtil.offsetMillisecond(new Date(), (int)expireTime));
        return JWT.create()
            .addPayloads(payload)
            .setKey(jwtSecret.getBytes())
            .sign();
    }
    
    /**
     * 验证并解析token
     * @throws AuthenticationException 如果token无效或已过期
     */
    public JWT validateToken(String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            // 验证token是否有效
            if (!JWTUtil.verify(token, jwtSecret.getBytes())) {
                throw new AuthenticationException("token无效");
            }
            
            // 验证token是否过期
            JWTValidator.of(token).validateDate();
            
            return JWT.of(token);
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthenticationException("token验证失败: " + e.getMessage());
        }
    }
    
    /**
     * 刷新token
     * 如果token即将过期,生成新的token
     */
    public String refreshToken(String token) {
        JWT jwt = validateToken(token);
        
        // 获取过期时间
        Date expireDate = DateUtil.date((Long)jwt.getPayload("exp"));
        // 如果token还有30分钟就过期,则刷新
        if (expireDate.getTime() - System.currentTimeMillis() < 30 * 60 * 1000) {
            Map<String, Object> payload = jwt.getPayloads();
            return generateToken(payload);
        }
        
        return token;
    }
} 