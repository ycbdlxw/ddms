# API接口文档

## 接口规范

### 1. 请求规范
- 接口采用RESTful风格
- 使用HTTPS协议
- 请求头需包含token
- 支持JSON格式数据

### 2. 响应规范
```json
{
    "code": 200,          // 状态码
    "message": "success", // 响应消息
    "data": {             // 响应数据
        // 具体数据结构
    },
    "timestamp": 1647856390123  // 时间戳
}
```

### 3. 状态码
| 状态码 | 说明 | 备注 |
|--------|------|------|
| 200 | 成功 | 请求成功处理 |
| 400 | 请求错误 | 参数验证失败 |
| 401 | 未授权 | token无效或过期 |
| 403 | 禁止访问 | 权限不足 |
| 404 | 资源不存在 | 请求的资源不存在 |
| 500 | 服务器错误 | 服务器内部错误 |

## 认证接口

### 1. 用户登录
```
POST /api/auth/login

请求体:
{
    "username": "admin",
    "password": "123456"
}

响应体:
{
    "code": 200,
    "message": "success",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiJ9...",
        "expires": 86400
    }
}
```

### 2. 用户注销
```
POST /api/auth/logout

请求头:
Authorization: Bearer {token}

响应体:
{
    "code": 200,
    "message": "success"
}
```

## 通用CRUD接口

### 1. 查询列表
```
GET /api/{table}/list

请求参数:
- page: 页码(默认1)
- size: 每页大小(默认10)
- sort: 排序字段
- order: 排序方式(asc/desc)
- [其他动态查询参数]

响应体:
{
    "code": 200,
    "data": {
        "total": 100,
        "list": [
            {
                // 数据项
            }
        ]
    }
}
```

### 2. 获取详情
```
GET /api/{table}/{id}

响应体:
{
    "code": 200,
    "data": {
        // 数据详情
    }
}
```

### 3. 创建记录
```
POST /api/{table}

请求体:
{
    // 创建的数据
}

响应体:
{
    "code": 200,
    "data": {
        "id": "新创建的ID"
    }
}
```

### 4. 更新记录
```
PUT /api/{table}/{id}

请求体:
{
    // 更新的数据
}

响应体:
{
    "code": 200,
    "message": "success"
}
```

### 5. 删除记录
```
DELETE /api/{table}/{id}

响应体:
{
    "code": 200,
    "message": "success"
}
```

## 批量操作接口

### 1. 批量创建
```
POST /api/{table}/batch

请求体:
{
    "list": [
        // 批量创建的数据
    ]
}

响应体:
{
    "code": 200,
    "data": {
        "ids": ["id1", "id2"]
    }
}
```

### 2. 批量更新
```
PUT /api/{table}/batch

请求体:
{
    "list": [
        {
            "id": "1",
            // 更新的数据
        }
    ]
}

响应体:
{
    "code": 200,
    "message": "success"
}
```

### 3. 批量删除
```
DELETE /api/{table}/batch

请求体:
{
    "ids": ["1", "2", "3"]
}

响应体:
{
    "code": 200,
    "message": "success"
}
```

## 特殊接口

### 1. 文件上传
```
POST /api/file/upload

请求头:
Content-Type: multipart/form-data

请求参数:
- file: 文件
- type: 文件类型

响应体:
{
    "code": 200,
    "data": {
        "url": "文件访问地址"
    }
}
```

### 2. 导出数据
```
GET /api/{table}/export

请求参数:
- format: 导出格式(xlsx/csv)
- [查询参数]

响应:
文件流
```

## 错误处理

### 1. 参数验证错误
```json
{
    "code": 400,
    "message": "参数验证失败",
    "data": {
        "field": "字段名",
        "message": "错误信息"
    }
}
```

### 2. 权限错误
```json
{
    "code": 403,
    "message": "没有权限执行此操作"
}
```

### 3. 业务错误
```json
{
    "code": 500,
    "message": "业务处理失败",
    "data": {
        "errorCode": "业务错误码",
        "errorMsg": "详细错误信息"
    }
}
```

## 版本控制

### 1. URL版本
```
/api/v1/users
/api/v2/users
```

### 2. 请求头版本
```
Accept: application/vnd.api.v1+json
Accept: application/vnd.api.v2+json
```

### 3. 参数版本
```
/api/users?version=1.0
/api/users?version=2.0
``` 