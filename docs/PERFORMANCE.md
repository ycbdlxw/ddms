# 性能优化指南

## JVM优化

### 1. 内存配置
```bash
# 堆内存配置
-Xms4g                # 初始堆大小
-Xmx4g                # 最大堆大小
-XX:MetaspaceSize=256m    # 元空间初始大小
-XX:MaxMetaspaceSize=512m # 元空间最大大小

# 年轻代配置
-Xmn2g                # 年轻代大小
-XX:SurvivorRatio=8   # Eden区与Survivor区比例

# 直接内存配置
-XX:MaxDirectMemorySize=2g
```

### 2. GC配置
```bash
# G1收集器配置
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200
-XX:G1HeapRegionSize=16m
-XX:InitiatingHeapOccupancyPercent=45

# GC日志配置
-XX:+PrintGCDetails
-XX:+PrintGCDateStamps
-Xloggc:/path/to/gc.log
```

### 3. 线程配置
```bash
# 线程栈大小
-Xss512k

# 本地方法栈
-XX:MaxJavaStackTraceDepth=10000

# 线程池配置
thread-pool:
  core-size: 10
  max-size: 20
  queue-capacity: 200
  keep-alive: 60
```

## 数据库优化

### 1. 索引优化
```sql
-- 创建合适的索引
CREATE INDEX idx_username ON users(username);
CREATE INDEX idx_create_time ON orders(create_time);
CREATE INDEX idx_user_status ON users(status, create_time);

-- 避免索引失效
SELECT * FROM users WHERE username LIKE 'admin%';  -- 走索引
SELECT * FROM users WHERE username LIKE '%admin';  -- 不走索引

-- 使用覆盖索引
SELECT username, status FROM users WHERE username = 'admin';
```

### 2. SQL优化
```sql
-- 避免SELECT *
SELECT id, username FROM users;

-- 使用EXISTS代替IN
SELECT * FROM orders WHERE user_id IN (SELECT id FROM users);  -- 不推荐
SELECT * FROM orders o WHERE EXISTS (SELECT 1 FROM users u WHERE u.id = o.user_id);  -- 推荐

-- 分页优化
SELECT * FROM users LIMIT 1000000, 10;  -- 不推荐
SELECT * FROM users WHERE id > (SELECT id FROM users LIMIT 1000000, 1) LIMIT 10;  -- 推荐
```

### 3. 配置优化
```properties
# 连接池配置
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=300000

# MySQL配置
innodb_buffer_pool_size=4G
innodb_log_file_size=1G
innodb_log_buffer_size=16M
innodb_flush_log_at_trx_commit=2
```

## 缓存优化

### 1. 多级缓存
```java
@Cacheable(value = "userCache", key = "#id")
public User getUser(Long id) {
    // 1. 查询本地缓存
    User user = localCache.get(id);
    if (user != null) {
        return user;
    }
    
    // 2. 查询Redis缓存
    user = redisTemplate.opsForValue().get("user:" + id);
    if (user != null) {
        localCache.put(id, user);
        return user;
    }
    
    // 3. 查询数据库
    user = userMapper.selectById(id);
    if (user != null) {
        redisTemplate.opsForValue().set("user:" + id, user);
        localCache.put(id, user);
    }
    
    return user;
}
```

### 2. 缓存策略
```java
// 缓存预热
@PostConstruct
public void warmUpCache() {
    List<User> users = userMapper.selectFrequentUsers();
    for (User user : users) {
        redisTemplate.opsForValue().set("user:" + user.getId(), user);
    }
}

// 缓存更新
@CachePut(value = "userCache", key = "#user.id")
public User updateUser(User user) {
    userMapper.updateById(user);
    return user;
}

// 缓存删除
@CacheEvict(value = "userCache", key = "#id")
public void deleteUser(Long id) {
    userMapper.deleteById(id);
}
```

### 3. Redis优化
```yaml
# Redis配置
spring:
  redis:
    # 连接池配置
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
        max-wait: -1ms
    # 集群配置
    cluster:
      nodes:
        - 127.0.0.1:7001
        - 127.0.0.1:7002
        - 127.0.0.1:7003
```

## 接口优化

### 1. 异步处理
```java
@Async
public CompletableFuture<User> getUserAsync(Long id) {
    return CompletableFuture.supplyAsync(() -> {
        return userMapper.selectById(id);
    });
}

// 并行处理
public List<User> getUsers(List<Long> ids) {
    List<CompletableFuture<User>> futures = ids.stream()
        .map(this::getUserAsync)
        .collect(Collectors.toList());
        
    return futures.stream()
        .map(CompletableFuture::join)
        .collect(Collectors.toList());
}
```

### 2. 数据压缩
```java
@GetMapping("/api/data")
public ResponseEntity<byte[]> getData() {
    // GZIP压缩
    byte[] data = getOriginalData();
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    GZIPOutputStream gzip = new GZIPOutputStream(bos);
    gzip.write(data);
    gzip.close();
    
    return ResponseEntity.ok()
        .header("Content-Encoding", "gzip")
        .body(bos.toByteArray());
}
```

### 3. 接口限流
```java
@RestController
public class ApiController {
    private RateLimiter rateLimiter = RateLimiter.create(100.0); // 每秒100个请求
    
    @GetMapping("/api/resource")
    public ResponseEntity<?> getResource() {
        if (!rateLimiter.tryAcquire()) {
            return ResponseEntity.status(429).body("Too Many Requests");
        }
        // 处理请求
    }
}
```

## 代码优化

### 1. 循环优化
```java
// 避免在循环中创建对象
StringBuilder sb = new StringBuilder();
for (String str : list) {
    sb.append(str);
}

// 使用Stream并行处理
list.parallelStream()
    .filter(str -> str.length() > 5)
    .map(String::toUpperCase)
    .collect(Collectors.toList());

// 批量处理
public void batchInsert(List<User> users) {
    for (int i = 0; i < users.size(); i += 1000) {
        int end = Math.min(i + 1000, users.size());
        userMapper.batchInsert(users.subList(i, end));
    }
}
```

### 2. 集合优化
```java
// 初始化时指定容量
List<User> users = new ArrayList<>(10000);
Map<Long, User> userMap = new HashMap<>(10000);

// 使用合适的集合
Set<Long> userIds = new HashSet<>();  // 去重
Queue<Task> tasks = new LinkedList<>();  // 队列
Map<String, User> userCache = new ConcurrentHashMap<>();  // 线程安全
```

### 3. 字符串优化
```java
// 使用StringBuilder
StringBuilder sb = new StringBuilder();
for (String str : list) {
    sb.append(str);
}

// 字符串常量
private static final String PREFIX = "user_";
public String getUserKey(Long id) {
    return PREFIX + id;
}
```

## 监控优化

### 1. JVM监控
```yaml
# Actuator配置
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus
  metrics:
    tags:
      application: ${spring.application.name}
```

### 2. 接口监控
```java
@Aspect
@Component
public class PerformanceMonitor {
    @Around("@annotation(monitor)")
    public Object monitor(ProceedingJoinPoint point) {
        long start = System.currentTimeMillis();
        Object result = point.proceed();
        long time = System.currentTimeMillis() - start;
        
        // 记录执行时间
        if (time > threshold) {
            log.warn("Slow API: {} took {}ms", point.getSignature(), time);
        }
        
        return result;
    }
}
```

### 3. 资源监控
```yaml
# Prometheus告警规则
groups:
- name: demo-service
  rules:
  - alert: HighCpuUsage
    expr: process_cpu_usage > 0.8
    for: 5m
    labels:
      severity: warning
    annotations:
      summary: High CPU usage detected
``` 