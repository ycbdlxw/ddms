# 开发环境搭建指南

## 环境要求

### 1. 基础环境
- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+
- Git 2.0+

### 2. IDE推荐
- IntelliJ IDEA
  - Ultimate版本(推荐)
  - Community版本
- 插件推荐
  - Lombok
  - MyBatis
  - Spring Boot
  - Maven Helper

### 3. 开发工具
- Postman/ApiPost
- Navicat/DBeaver
- Redis Desktop Manager
- Git客户端

## 环境安装

### 1. JDK安装
```bash
# Ubuntu/Debian
apt install openjdk-17-jdk

# CentOS
yum install java-17-openjdk-devel

# 验证安装
java -version
```

### 2. Maven安装
```bash
# 下载Maven
wget https://downloads.apache.org/maven/maven-3/3.8.8/binaries/apache-maven-3.8.8-bin.tar.gz

# 解压安装
tar -xzf apache-maven-3.8.8-bin.tar.gz
mv apache-maven-3.8.8 /usr/local/maven

# 配置环境变量
echo 'export MAVEN_HOME=/usr/local/maven' >> ~/.bashrc
echo 'export PATH=$PATH:$MAVEN_HOME/bin' >> ~/.bashrc
source ~/.bashrc

# 验证安装
mvn -version
```

### 3. MySQL安装
```bash
# Ubuntu/Debian
apt install mysql-server-8.0

# CentOS
yum install mysql-server

# 启动服务
systemctl start mysqld

# 初始化配置
mysql_secure_installation
```

### 4. Redis安装
```bash
# Ubuntu/Debian
apt install redis-server

# CentOS
yum install redis

# 启动服务
systemctl start redis

# 验证安装
redis-cli ping
```

## 项目配置

### 1. 代码获取
```bash
# 克隆项目
git clone https://github.com/your-org/demo-service.git

# 切换分支
git checkout develop
```

### 2. Maven配置
```xml
<!-- settings.xml -->
<settings>
    <mirrors>
        <mirror>
            <id>aliyun</id>
            <name>aliyun maven</name>
            <url>https://maven.aliyun.com/repository/public</url>
            <mirrorOf>central</mirrorOf>
        </mirror>
    </mirrors>
</settings>
```

### 3. IDE配置
1. 导入项目
   - File -> Open
   - 选择项目目录
   - 等待依赖下载
   
2. 插件安装
   - Settings -> Plugins
   - 搜索并安装推荐插件
   
3. 代码格式化
   - Settings -> Editor -> Code Style
   - 导入代码样式配置

## 数据库配置

### 1. 创建数据库
```sql
-- 创建数据库
CREATE DATABASE demo DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 创建用户
CREATE USER 'demo'@'localhost' IDENTIFIED BY 'demo123';

-- 授权
GRANT ALL PRIVILEGES ON demo.* TO 'demo'@'localhost';
FLUSH PRIVILEGES;
```

### 2. 初始化数据
```bash
# 执行SQL脚本
mysql -u demo -p demo < init.sql

# 或在IDE中执行
# 1. 打开database工具窗口
# 2. 连接数据库
# 3. 执行init.sql脚本
```

### 3. 测试数据
```sql
-- 插入测试数据
INSERT INTO users (username, password) VALUES ('admin', 'admin123');
INSERT INTO users (username, password) VALUES ('test', 'test123');
```

## 本地配置

### 1. 应用配置
```yaml
# application-local.yml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/demo
    username: demo
    password: demo123
    
  redis:
    host: localhost
    port: 6379
```

### 2. 日志配置
```yaml
logging:
  level:
    root: INFO
    com.ycbd.demo: DEBUG
  file:
    name: logs/demo-service.log
```

### 3. 其他配置
```yaml
# 自定义配置
demo:
  upload-path: D:/upload
  temp-path: D:/temp
  
# 开发者特定配置
dev:
  mock-enabled: true
  debug-mode: true
```

## 启动运行

### 1. 依赖安装
```bash
# 安装依赖
mvn clean install

# 跳过测试
mvn clean install -DskipTests
```

### 2. 本地启动
```bash
# 命令行启动
mvn spring-boot:run -Dspring.profiles.active=local

# IDE启动
# 1. 配置Run Configuration
# 2. 添加VM参数: -Dspring.profiles.active=local
# 3. 运行Application类
```

### 3. 验证部署
```bash
# 检查服务状态
curl http://localhost:8080/actuator/health

# 测试接口
curl http://localhost:8080/api/test
```

## 开发规范

### 1. 代码规范
- 遵循阿里巴巴Java开发手册
- 使用统一的代码格式化配置
- 遵循项目既定的架构规范
- 编写单元测试

### 2. Git规范
- 分支管理
  - master: 生产环境分支
  - develop: 开发环境分支
  - feature/*: 功能分支
  - hotfix/*: 紧急修复分支
  
- 提交规范
  - feat: 新功能
  - fix: 修复bug
  - docs: 文档更新
  - style: 代码格式
  - refactor: 重构
  - test: 测试相关
  - chore: 其他修改

### 3. 文档规范
- 及时更新文档
- 注释完整规范
- 提供接口文档
- 编写使用说明 