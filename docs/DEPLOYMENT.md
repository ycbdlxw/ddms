# 部署指南

## 环境要求

### 1. 硬件要求
- CPU: 4核8线程
- 内存: 8GB以上
- 磁盘: 100GB以上
- 网络: 100Mbps以上

### 2. 软件要求
- JDK 17+
- MySQL 8.0+
- Redis 6.0+
- Nginx 1.20+
- Docker 20.10+
- Docker Compose 2.0+

### 3. 操作系统
- CentOS 7+
- Ubuntu 20.04+
- Debian 10+

## 部署方式

### 1. 直接部署
```bash
# 1. 安装JDK
apt install openjdk-17-jdk

# 2. 安装MySQL
apt install mysql-server-8.0

# 3. 安装Redis
apt install redis-server

# 4. 安装Nginx
apt install nginx

# 5. 部署应用
java -jar demo-service.jar
```

### 2. Docker部署
```yaml
# docker-compose.yml
version: '3'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: demo
    volumes:
      - mysql-data:/var/lib/mysql
    ports:
      - "3306:3306"

  redis:
    image: redis:6.0
    volumes:
      - redis-data:/data
    ports:
      - "6379:6379"

  app:
    image: demo-service:latest
    environment:
      SPRING_PROFILES_ACTIVE: prod
      MYSQL_HOST: mysql
      REDIS_HOST: redis
    ports:
      - "8080:8080"
    depends_on:
      - mysql
      - redis

volumes:
  mysql-data:
  redis-data:
```

### 3. Kubernetes部署
```yaml
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: demo-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: demo-service
  template:
    metadata:
      labels:
        app: demo-service
    spec:
      containers:
      - name: demo-service
        image: demo-service:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: prod
```

## 部署步骤

### 1. 准备工作
1. 安装必要软件
2. 准备配置文件
3. 初始化数据库
4. 配置环境变量

### 2. 应用部署
1. 上传应用包
2. 配置服务
3. 启动应用
4. 验证部署

### 3. 负载均衡
```nginx
# nginx.conf
upstream demo_service {
    server 127.0.0.1:8080;
    server 127.0.0.1:8081;
    server 127.0.0.1:8082;
}

server {
    listen 80;
    server_name demo.example.com;

    location / {
        proxy_pass http://demo_service;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 监控告警

### 1. 应用监控
- JVM监控
- 线程监控
- API监控
- 性能监控

### 2. 系统监控
- CPU使用率
- 内存使用率
- 磁盘使用率
- 网络流量

### 3. 告警配置
- 邮件告警
- 短信告警
- 钉钉告警
- 微信告警

## 日志管理

### 1. 日志配置
```yaml
logging:
  file:
    name: /var/log/demo-service.log
  level:
    root: INFO
    com.ycbd.demo: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

### 2. 日志收集
- ELK Stack
- Filebeat
- Logstash
- Elasticsearch
- Kibana

### 3. 日志分析
- 错误统计
- 性能分析
- 访问统计
- 安全分析

## 备份恢复

### 1. 数据备份
```bash
# MySQL备份
mysqldump -u root -p demo > backup.sql

# Redis备份
redis-cli save

# 应用配置备份
tar -czf config-backup.tar.gz /etc/demo-service/
```

### 2. 定时备份
```bash
# crontab配置
0 2 * * * /usr/local/bin/backup.sh
```

### 3. 数据恢复
```bash
# MySQL恢复
mysql -u root -p demo < backup.sql

# Redis恢复
redis-cli shutdown
cp dump.rdb /var/lib/redis/
service redis start
```

## 性能优化

### 1. JVM优化
```bash
JAVA_OPTS="\
    -Xms4g \
    -Xmx4g \
    -XX:+UseG1GC \
    -XX:MaxGCPauseMillis=200 \
    -XX:+HeapDumpOnOutOfMemoryError"
```

### 2. 数据库优化
- 索引优化
- SQL优化
- 连接池优化
- 主从复制

### 3. 缓存优化
- 本地缓存
- Redis缓存
- 多级缓存
- 缓存预热

## 安全加固

### 1. 系统加固
- 关闭不必要服务
- 修改默认端口
- 配置防火墙
- 限制访问权限

### 2. 应用加固
- HTTPS配置
- SQL注入防护
- XSS防护
- CSRF防护

### 3. 数据加固
- 数据加密
- 访问控制
- 审计日志
- 备份策略 