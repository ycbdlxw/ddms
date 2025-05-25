# 开发指南

## 配置驱动开发

### 1. 表属性配置
```sql
CREATE TABLE table_attribute (
    tableName varchar(100) COMMENT '数据库中文表名',
    dbtable varchar(100) NOT NULL COMMENT '数据库表名',
    sort varchar(200) COMMENT '排序内容',
    functions varchar(100) COMMENT '功能模块'
);
```

使用示例:
```sql
INSERT INTO table_attribute 
(tableName, dbtable, sort, functions)
VALUES 
('用户表', 'sys_user', 'id DESC', '用户管理');
```

### 2. 字段属性配置
```sql
CREATE TABLE column_attribute (
    dbTableName varchar(100) NOT NULL COMMENT '数据库表名',
    name varchar(100) NOT NULL COMMENT '字段名称',
    pagename varchar(100) COMMENT '名称',
    IsShowInList tinyint(1) DEFAULT 0 COMMENT '列表显示'
);
```

使用示例:
```sql
INSERT INTO column_attribute 
(dbTableName, name, pagename, IsShowInList)
VALUES 
('sys_user', 'username', '用户名', 1);
```

### 3. 字段校验配置
```sql
CREATE TABLE column_check_property (
    check_table VARCHAR(50) NOT NULL COMMENT '检查表名',
    check_column VARCHAR(50) NOT NULL COMMENT '列名',
    check_mode VARCHAR(50) NOT NULL COMMENT '检查模式',
    errorMsg VARCHAR(255) NOT NULL COMMENT '错误信息'
);
```

使用示例:
```sql
INSERT INTO column_check_property 
(check_table, check_column, check_mode, errorMsg)
VALUES 
('sys_user', 'username', 'isNotExit', '用户名已存在');
```

## MyBatis使用规范

### SQL标准化
所有SQL操作必须遵循三段式结构:

1. SELECT: 字段选择
```xml
<select id="queryList" resultType="Map">
    SELECT ${columns}
    FROM ${table}
    <where>
        <if test="whereString != null">
            ${whereString}
        </if>
    </where>
</select>
```

2. FROM: 表关联
- 支持多表关联
- 通过配置管理关联关系
- 动态构建JOIN语句

3. WHERE: 条件筛选
- 支持动态条件组合
- 配置化的查询条件
- 灵活的参数绑定

### 开发规范
1. 配置优先
   - 新功能优先考虑配置实现
   - 避免硬编码业务规则
   - 保持代码简洁统一

2. 数据处理
   - 统一使用配置表定义规则
   - 遵循验证顺序
   - 错误信息配置化

3. 性能优化
   - 减少不必要的函数调用
   - 优化SQL查询
   - 合理使用缓存

## API接口规范

### 1. 通用响应格式
```json
{
    "code": 200,      // 响应状态码
    "result": {},     // 响应结果数据
    "message": null,  // 响应消息
    "timestamp": 1642612345678  // 响应时间戳
}
```

### 2. 状态码规范
- 200: 成功
- 400: 请求参数错误
- 401: 未登录或token失效
- 403: 无权限
- 404: 资源不存在
- 500: 服务器内部错误

### 3. 接口命名规范
- 使用RESTful风格
- 路径使用小写字母
- 使用连字符(-)分隔单词
- 避免使用特殊字符

## 开发流程

### 1. 配置阶段
1. 设计数据表结构
2. 配置表属性(table_attribute)
3. 配置字段属性(column_attribute)
4. 配置校验规则(column_check_property)

### 2. 开发阶段
1. 遵循"配置优先"原则
2. 编写必要的业务逻辑
3. 进行单元测试
4. 运行自动化测试

### 3. 测试阶段
1. 执行测试脚本
2. 验证功能完整性
3. 检查性能指标
4. 生成测试报告

### 4. 部署阶段
1. 准备部署环境
2. 配置数据库连接
3. 初始化配置数据
4. 启动应用服务 