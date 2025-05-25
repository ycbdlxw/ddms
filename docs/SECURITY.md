# 安全指南

## 安全架构

### 1. 安全框架
- Spring Security
- JWT认证
- 权限控制
- 数据加密

### 2. 安全层次
1. 网络安全
   - HTTPS
   - 防火墙
   - WAF
   
2. 应用安全
   - 认证授权
   - 输入验证
   - 会话管理
   
3. 数据安全
   - 加密存储
   - 传输加密
   - 访问控制

## 认证授权

### 1. JWT认证
```java
@Configuration
public class JwtConfig {
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    public String generateToken(String username) {
        return Jwts.builder()
            .setSubject(username)
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact();
    }
}
```

### 2. 权限控制
```java
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/admin")
public ResponseEntity<?> adminApi() {
    // 管理员接口
}

@PreAuthorize("hasAnyRole('USER','ADMIN')")
@GetMapping("/user")
public ResponseEntity<?> userApi() {
    // 用户接口
}
```

### 3. 会话管理
```java
@Configuration
public class SessionConfig {
    @Bean
    public HttpSessionListener httpSessionListener() {
        return new HttpSessionListener() {
            @Override
            public void sessionCreated(HttpSessionEvent se) {
                // 会话创建处理
            }
            
            @Override
            public void sessionDestroyed(HttpSessionEvent se) {
                // 会话销毁处理
            }
        };
    }
}
```

## 数据安全

### 1. 数据加密
```java
@Component
public class EncryptionUtil {
    @Value("${encryption.key}")
    private String key;
    
    public String encrypt(String data) {
        // AES加密实现
    }
    
    public String decrypt(String encryptedData) {
        // AES解密实现
    }
}
```

### 2. 敏感数据处理
```java
@Entity
public class User {
    @Encrypted
    private String password;
    
    @Masked
    private String phone;
    
    @Sensitive
    private String idCard;
}
```

### 3. 数据脱敏
```java
public class DataMaskUtil {
    public static String maskPhone(String phone) {
        return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }
    
    public static String maskIdCard(String idCard) {
        return idCard.replaceAll("(\\d{6})\\d{8}(\\w{4})", "$1********$2");
    }
}
```

## 安全防护

### 1. SQL注入防护
```java
// 使用参数化查询
@Select("SELECT * FROM users WHERE username = #{username}")
User findByUsername(@Param("username") String username);

// 避免直接拼接SQL
@Select("SELECT * FROM ${table} WHERE id = #{id}")  // 错误示例
@Select("SELECT * FROM users WHERE id = #{id}")     // 正确示例
```

### 2. XSS防护
```java
@Component
public class XssFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
            FilterChain chain) throws IOException, ServletException {
        chain.doFilter(new XssHttpServletRequestWrapper((HttpServletRequest) request), 
                response);
    }
}

public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (values == null) {
            return null;
        }
        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = cleanXSS(values[i]);
        }
        return encodedValues;
    }
    
    private String cleanXSS(String value) {
        // XSS清理实现
    }
}
```

### 3. CSRF防护
```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .ignoringAntMatchers("/login", "/logout");
    }
}
```

## 安全审计

### 1. 操作日志
```java
@Aspect
@Component
public class AuditLogAspect {
    @Around("@annotation(auditLog)")
    public Object around(ProceedingJoinPoint point, AuditLog auditLog) {
        // 记录操作日志
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String operation = auditLog.operation();
        String method = point.getSignature().getName();
        
        // 保存审计日志
        saveAuditLog(username, operation, method);
        
        return point.proceed();
    }
}
```

### 2. 登录日志
```java
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
            HttpServletResponse response, Authentication authentication) {
        // 记录登录日志
        String username = authentication.getName();
        String ip = request.getRemoteAddr();
        Date loginTime = new Date();
        
        // 保存登录日志
        saveLoginLog(username, ip, loginTime);
    }
}
```

### 3. 安全审计
```java
@Service
public class SecurityAuditService {
    public void auditUserOperation(String username, String operation, 
            String resource) {
        SecurityAudit audit = new SecurityAudit();
        audit.setUsername(username);
        audit.setOperation(operation);
        audit.setResource(resource);
        audit.setTimestamp(new Date());
        audit.setIp(getCurrentIp());
        
        // 保存审计记录
        securityAuditRepository.save(audit);
    }
}
```

## 安全配置

### 1. 密码策略
```yaml
security:
  password:
    min-length: 8
    require-digit: true
    require-lowercase: true
    require-uppercase: true
    require-special-char: true
    history-count: 5
    max-age-days: 90
```

### 2. 会话配置
```yaml
security:
  session:
    timeout: 1800
    max-sessions: 1
    prevent-concurrent: true
```

### 3. 安全响应头
```yaml
security:
  headers:
    xss-protection: 1; mode=block
    content-security-policy: default-src 'self'
    x-frame-options: DENY
    strict-transport-security: max-age=31536000; includeSubDomains
```

## 应急响应

### 1. 安全监控
- 实时监控异常登录
- 监控敏感操作
- 监控数据访问
- 监控系统资源

### 2. 告警处理
- 设置告警阈值
- 配置告警通知
- 记录告警日志
- 分析告警原因

### 3. 应急预案
- 账号锁定
- 服务降级
- 数据备份
- 系统恢复 