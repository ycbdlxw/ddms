# 运维手册

## 日常运维

### 1. 服务管理
```bash
# 启动服务
systemctl start demo-service

# 停止服务
systemctl stop demo-service

# 重启服务
systemctl restart demo-service

# 查看服务状态
systemctl status demo-service
```

### 2. 日志查看
```bash
# 实时查看日志
tail -f /var/log/demo-service.log

# 查看错误日志
grep ERROR /var/log/demo-service.log

# 查看指定时间段日志
sed -n '/2024-03-21 10:00/,/2024-03-21 11:00/p' /var/log/demo-service.log
```

### 3. 性能监控
```bash
# CPU使用率
top -p $(pgrep -f demo-service)

# 内存使用
free -h

# 磁盘使用
df -h

# 网络连接
netstat -anp | grep demo-service
```

## 问题排查

### 1. 常见问题
1. 服务无法启动
   - 检查配置文件
   - 检查端口占用
   - 检查日志错误
   
2. 接口响应慢
   - 检查CPU使用率
   - 检查内存使用
   - 检查数据库连接
   
3. 内存溢出
   - 分析堆内存使用
   - 检查内存泄漏
   - 调整JVM参数

### 2. 排查工具
```bash
# JVM堆内存分析
jmap -dump:format=b,file=heap.bin $(pgrep -f demo-service)

# 线程堆栈分析
jstack $(pgrep -f demo-service) > thread.dump

# GC日志分析
jstat -gc $(pgrep -f demo-service) 1000
```

### 3. SQL分析
```sql
-- 慢查询分析
SELECT * FROM mysql.slow_log;

-- 执行计划分析
EXPLAIN SELECT * FROM users WHERE username = 'admin';

-- 连接状态查看
SHOW PROCESSLIST;
```

## 数据维护

### 1. 数据备份
```bash
# 全量备份
mysqldump -u root -p demo > backup_$(date +%Y%m%d).sql

# 增量备份
mysqlbinlog mysql-bin.* > incremental.sql

# 配置备份
tar -czf config_$(date +%Y%m%d).tar.gz /etc/demo-service/
```

### 2. 数据恢复
```bash
# 全量恢复
mysql -u root -p demo < backup.sql

# 增量恢复
mysql -u root -p demo < incremental.sql

# 配置恢复
tar -xzf config.tar.gz -C /etc/demo-service/
```

### 3. 数据清理
```sql
-- 清理历史数据
DELETE FROM operation_log WHERE create_time < DATE_SUB(NOW(), INTERVAL 30 DAY);

-- 清理临时数据
TRUNCATE TABLE temp_data;

-- 优化表空间
OPTIMIZE TABLE users;
```

## 系统优化

### 1. JVM优化
```bash
# 内存配置
JAVA_OPTS="-Xms4g -Xmx4g -XX:MetaspaceSize=256m"

# GC配置
JAVA_OPTS="$JAVA_OPTS -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# 调试配置
JAVA_OPTS="$JAVA_OPTS -XX:+HeapDumpOnOutOfMemoryError"
```

### 2. 数据库优化
```sql
-- 索引优化
CREATE INDEX idx_username ON users(username);

-- 表结构优化
ALTER TABLE users MODIFY COLUMN status TINYINT;

-- 参数优化
SET GLOBAL innodb_buffer_pool_size = 4G;
```

### 3. 缓存优化
```yaml
# Redis配置
spring:
  redis:
    maxTotal: 100
    maxIdle: 10
    minIdle: 5
    maxWaitMillis: 2000
```

## 监控告警

### 1. 系统监控
```yaml
# Prometheus配置
management:
  endpoints:
    web:
      exposure:
        include: prometheus,health,info
  metrics:
    tags:
      application: ${spring.application.name}
```

### 2. 业务监控
```java
@Aspect
@Component
public class MetricsAspect {
    @Around("@annotation(metrics)")
    public Object around(ProceedingJoinPoint point) {
        long startTime = System.currentTimeMillis();
        try {
            return point.proceed();
        } finally {
            long endTime = System.currentTimeMillis();
            // 记录接口耗时
            recordMetrics(point.getSignature().getName(), endTime - startTime);
        }
    }
}
```

### 3. 告警配置
```yaml
# 告警规则
alerting:
  rules:
    - alert: HighCpuUsage
      expr: cpu_usage > 80
      for: 5m
      labels:
        severity: warning
      annotations:
        summary: High CPU usage detected
```

## 应急处理

### 1. 服务降级
```java
@HystrixCommand(fallbackMethod = "fallback")
public Object service() {
    // 正常服务逻辑
}

public Object fallback() {
    // 降级处理逻辑
}
```

### 2. 限流控制
```java
@RateLimiter(value = 100)
public Object apiMethod() {
    // 接口逻辑
}
```

### 3. 应急预案
1. 系统宕机
   - 切换备用系统
   - 恢复数据备份
   - 通知相关人员
   
2. 数据异常
   - 停止相关服务
   - 数据回滚
   - 排查原因
   
3. 安全事件
   - 阻断攻击源
   - 清理恶意数据
   - 加固安全措施

## 运维规范

### 1. 发布规范
- 准备发布计划
- 备份当前版本
- 灰度发布
- 回滚机制

### 2. 值班规范
- 值班安排
- 交接流程
- 应急处理
- 上报机制

### 3. 文档规范
- 操作文档
- 问题记录
- 优化建议
- 经验总结 