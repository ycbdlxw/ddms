# 配置指南

## 配置体系

### 1. 配置分类
系统配置分为以下几类:
1. 应用配置
   - 服务端口
   - 数据源
   - 日志级别
   
2. 业务配置
   - 表属性配置
   - 字段属性配置
   - 校验规则配置
   
3. 环境配置
   - 开发环境
   - 测试环境
   - 生产环境

### 2. 配置文件
```
resources/
├── application.yml          # 主配置文件
├── application-dev.yml     # 开发环境配置
├── application-test.yml    # 测试环境配置
└── application-prod.yml    # 生产环境配置
```

## 应用配置

### 1. 基础配置
```yaml
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  application:
    name: demo-service
```

### 2. 数据源配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/demo
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### 3. MyBatis配置
```yaml
mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.ycbd.demo.model
  configuration:
    map-underscore-to-camel-case: true
```

## 环境配置

### 1. 开发环境(application-dev.yml)
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/demo_dev
    
logging:
  level:
    root: DEBUG
```

### 2. 测试环境(application-test.yml)
```yaml
spring:
  datasource:
    url: jdbc:mysql://test-server:3306/demo_test
    
logging:
  level:
    root: INFO
```

### 3. 生产环境(application-prod.yml)
```yaml
spring:
  datasource:
    url: jdbc:mysql://prod-server:3306/demo_prod
    
logging:
  level:
    root: WARN
```

## 业务配置

### 1. JWT配置
```yaml
jwt:
  secret: your-secret-key
  expiration: 86400000  # 24小时
  header: Authorization
```

### 2. 缓存配置
```yaml
spring:
  cache:
    type: redis
  redis:
    host: localhost
    port: 6379
```

### 3. 线程池配置
```yaml
thread-pool:
  core-size: 10
  max-size: 20
  queue-capacity: 200
  keep-alive: 60
```

## 环境变量

### 1. 必需环境变量
- `MYSQL_HOST`: 数据库主机
- `MYSQL_PORT`: 数据库端口
- `MYSQL_USER`: 数据库用户名
- `MYSQL_PASS`: 数据库密码
- `JWT_SECRET`: JWT密钥

### 2. 可选环境变量
- `SERVER_PORT`: 服务端口
- `LOG_LEVEL`: 日志级别
- `REDIS_HOST`: Redis主机
- `REDIS_PORT`: Redis端口

### 3. 环境变量使用
```yaml
spring:
  datasource:
    url: jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/demo
    username: ${MYSQL_USER}
    password: ${MYSQL_PASS}
```

## 配置优先级

### 1. 优先级顺序(从高到低)
1. 命令行参数
2. 环境变量
3. application-{profile}.yml
4. application.yml
5. 默认配置

### 2. 配置覆盖
- 高优先级配置会覆盖低优先级配置
- 同文件中后面的配置覆盖前面的配置
- 特定环境配置覆盖通用配置

### 3. 最佳实践
- 敏感信息使用环境变量
- 环境相关配置使用profile
- 通用配置放在主配置文件

## 配置管理

### 1. 配置更新
- 配置文件修改
- 环境变量设置
- 运行时动态更新

### 2. 配置验证
- 启动时校验
- 运行时检查
- 配置项测试

### 3. 配置安全
- 敏感信息加密
- 权限控制
- 审计日志 