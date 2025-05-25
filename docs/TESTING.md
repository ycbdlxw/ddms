# 自动化测试服务指南

## 测试服务概述

自动化测试服务是一个基于配置驱动的测试框架,通过配置文件定义测试用例和预期结果,自动执行测试并生成报告。主要特点:

- 配置驱动测试
- 自动化执行
- 报告生成
- 历史记录比对

## 测试配置说明

### 1. 测试用例配置
测试用例通过JSON配置文件定义,包含以下主要部分:

```json
{
  "name": "用户登录测试",
  "description": "测试用户登录功能",
  "api": "/api/login",
  "method": "POST",
  "headers": {
    "Content-Type": "application/json"
  },
  "body": {
    "username": "admin",
    "password": "123456"
  },
  "expects": {
    "status": 200,
    "body": {
      "code": 0,
      "message": "success"
    }
  }
}
```

### 2. 测试场景配置
场景配置用于组织多个相关的测试用例:

```json
{
  "name": "用户管理测试",
  "cases": [
    "login_test.json",
    "create_user_test.json",
    "update_user_test.json",
    "delete_user_test.json"
  ],
  "setup": {
    "sql": [
      "DELETE FROM sys_user WHERE username = 'test_user'",
      "INSERT INTO sys_user (username, password) VALUES ('test_user', '123456')"
    ]
  },
  "teardown": {
    "sql": [
      "DELETE FROM sys_user WHERE username = 'test_user'"
    ]
  }
}
```

## 测试执行

### 1. 执行单个测试
```bash
./test.sh -f test_cases/login_test.json
```

### 2. 执行测试场景
```bash
./test.sh -s scenarios/user_management.json
```

### 3. 执行全部测试
```bash
./test.sh --all
```

## 测试报告

### 1. 报告格式
测试报告以HTML格式生成,包含以下内容:

- 测试概要
  - 总用例数
  - 通过数
  - 失败数
  - 执行时间
- 详细结果
  - 测试用例名称
  - 请求信息
  - 实际响应
  - 期望结果
  - 测试结果
  - 错误信息(如果有)
- 历史对比
  - 与上次测试结果对比
  - 性能趋势分析

### 2. 报告示例

```html
<!DOCTYPE html>
<html>
<head>
    <title>测试报告 - 2024-03-21</title>
</head>
<body>
    <h1>测试报告</h1>
    <div class="summary">
        <p>总用例数: 100</p>
        <p>通过: 98</p>
        <p>失败: 2</p>
        <p>执行时间: 1分30秒</p>
    </div>
    <div class="details">
        <h2>测试详情</h2>
        <table>
            <tr>
                <th>用例名称</th>
                <th>结果</th>
                <th>耗时</th>
            </tr>
            <tr>
                <td>用户登录测试</td>
                <td class="pass">通过</td>
                <td>0.5s</td>
            </tr>
            <!-- 更多测试用例结果 -->
        </table>
    </div>
</body>
</html>
```

## 最佳实践

### 1. 测试用例设计
- 每个测试用例专注于一个功能点
- 包含正向和异常测试场景
- 使用有意义的测试数据
- 清晰的测试描述

### 2. 测试数据管理
- 使用专门的测试数据库
- 测试前后清理测试数据
- 避免使用生产环境数据
- 使用mock数据模拟外部依赖

### 3. 测试执行策略
- 定期执行全量测试
- 代码提交时执行相关测试
- 发布前执行回归测试
- 监控测试执行时间

## 常见问题

### 1. 测试失败排查
1. 检查测试环境
2. 验证测试数据
3. 查看详细错误日志
4. 对比历史测试记录

### 2. 性能问题
1. 优化测试用例执行顺序
2. 合理设置超时时间
3. 并行执行无依赖的测试
4. 监控系统资源使用

### 3. 维护建议
1. 定期更新测试用例
2. 删除过时的测试
3. 优化低效的测试
4. 完善测试文档 