# 配置驱动型后端服务系统

## 项目愿景
本项目致力于通过"数据驱动一切"的理念,创建一个高度灵活、易维护且功能强大的后端服务系统。通过配置化的方式驱动业务逻辑和数据库操作,最大限度地减少硬编码,提高系统的可维护性和扩展性。

## 核心理念
- **数据驱动一切**: 所有的业务逻辑和数据库操作都通过配置来驱动
- **配置优于编码**: 能用配置实现的功能,绝不使用硬编码
- **标准化操作**: 统一的接口设计和数据处理流程
- **自动化测试**: 完整的测试服务支持

## 四大设计原则
1. **最小化文件数量**: 保持项目结构简洁,避免过度拆分
2. **最小化代码量**: 通过配置驱动减少代码编写
3. **最小化函数调用**: 优化性能,减少调用链路
4. **配置优先原则**: 优先通过配置实现功能

## 技术特色

### 1. 三层配置驱动
- **表属性配置(table_attribute)**: 管理表级行为
- **字段属性配置(column_attribute)**: 管理字段级行为
- **字段校验配置(column_check_property)**: 管理校验规则

### 2. MyBatis配置化查询
- SQL标准化(SELECT/FROM/WHERE三段式)
- 动态SQL构建
- 配置驱动的查询条件

### 3. 自动化测试服务
- 配置驱动的测试用例
- 自动化测试执行
- 测试报告生成

## 技术栈
- Spring Boot 3.0
- MyBatis
- MySQL
- JWT
- Hutool工具集

## 项目结构
```
project/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/ycbd/demo/
│   │   │       ├── config/
│   │   │       ├── controller/
│   │   │       ├── interceptor/
│   │   │       ├── mapper/
│   │   │       ├── model/
│   │   │       └── service/
│   │   └── resources/
│   │       ├── mapper/
│   │       ├── test/
│   │       └── application.yml
│   └── test/
├── docs/
│   ├── DEVELOPMENT.md
│   ├── DATABASE.md
│   └── TESTING.md
└── pom.xml
```

## 快速开始
1. 克隆项目
2. 配置数据库连接
3. 初始化配置表
4. 运行测试服务
5. 开始开发

详细说明请参考[开发指南](DEVELOPMENT.md)。 