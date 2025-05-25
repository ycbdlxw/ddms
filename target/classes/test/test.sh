#!/bin/bash

# 一、登录测试场景
# 测试1: 用户不存在
curl -s -X POST 'http://localhost:8080/api/common/login' \
  -H 'Content-Type: application/json' \
  -d '{"username":"nonexist_user","password":"any_password"}'

# 测试2: 密码错误
curl -s -X POST 'http://localhost:8080/api/common/login' \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"wrong_password"}'

# 测试3: 登录成功
curl -s -X POST 'http://localhost:8080/api/common/login' \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"ycbd1234"}'

# 二、SQL注入防护测试
# 测试4: 用户名包含SQL注入字符
curl -s -X POST 'http://localhost:8080/api/common/save' \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer ${TOKEN}' \
  -d '{"targetTable":"sys_user","username":"test_user_sql","password":"123456","real_name":"SQL注入测试","status":1}'

# 测试5: 密码包含SQL注入字符
curl -s -X POST 'http://localhost:8080/api/common/save' \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer ${TOKEN}' \
  -d '{"targetTable":"sys_user","username":"test_user2","password":"123456_sql","real_name":"SQL注入测试","status":1}'

# 测试6: 其他字段包含SQL注入字符
curl -s -X POST 'http://localhost:8080/api/common/save' \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer ${TOKEN}' \
  -d '{"targetTable":"sys_user","username":"test_user3","password":"123456","real_name":"测试用户_sql","email":"test@example.com","status":1}'

# 三、字段验证测试
# 测试7: 缺少必填字段(用户名)
curl -s -X POST 'http://localhost:8080/api/common/save' \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer ${TOKEN}' \
  -d '{"targetTable":"sys_user","password":"123456","real_name":"测试用户","status":1}'

# 测试8: 缺少必填字段(密码)
curl -s -X POST 'http://localhost:8080/api/common/save' \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer ${TOKEN}' \
  -d '{"targetTable":"sys_user","username":"test_user4","real_name":"测试用户","status":1}'

# 测试9: 邮箱格式错误
curl -s -X POST 'http://localhost:8080/api/common/save' \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer ${TOKEN}' \
  -d '{"targetTable":"sys_user","username":"test_user5","password":"123456","real_name":"测试用户","email":"invalid_email","status":1}'

# 测试10: 手机号格式错误
curl -s -X POST 'http://localhost:8080/api/common/save' \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer ${TOKEN}' \
  -d '{"targetTable":"sys_user","username":"test_user6","password":"123456","real_name":"测试用户","phone":"invalid_phone","status":1}'

# 测试11: 重复注册(使用已存在的用户名)
curl -s -X POST 'http://localhost:8080/api/common/save' \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer ${TOKEN}' \
  -d '{"targetTable":"sys_user","username":"admin","password":"123456","real_name":"测试用户","status":1}'

# 四、完整的用户管理流程测试
# 测试12: 成功注册新用户
curl -s -X POST 'http://localhost:8080/api/common/save' \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer ${TOKEN}' \
  -d '{"targetTable":"sys_user","username":"test_complete_user","password":"test123456","real_name":"完整测试用户","phone":"13800138001","email":"test_complete@example.com","org_id":1,"role_ids":"1,2","age":25,"status":1}'

# 测试13: 查询新用户
curl -s -X GET "http://localhost:8080/api/common/list?pageIndex=0&pageSize=20&targetTable=sys_user&columns=username,real_name,phone,email&username=test_complete_user" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer ${TOKEN}"

# 测试14: 修改用户信息
curl -s -X POST 'http://localhost:8080/api/common/save' \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer ${TOKEN}' \
  -d '{"targetTable":"sys_user","username":"test_complete_user","real_name":"已修改的测试用户","phone":"13800138002","email":"test_complete_updated@example.com","age":26,"status":1}'

# 测试15: 查询修改后的用户信息
curl -s -X GET "http://localhost:8080/api/common/list?pageIndex=0&pageSize=20&targetTable=sys_user&columns=username,real_name,phone,email&username=test_complete_user" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer ${TOKEN}"

# 测试16: 删除用户
curl -X POST 'http://localhost:8080/api/common/delete' \
  -H 'Content-Type: application/json' \
   -H "Authorization: Bearer ${TOKEN}"\
  -d '{
    "targetTable": "sys_user",
    "username": "test_complete_user"
}'

# 测试17: 确认用户已删除
curl -X GET "http://localhost:8080/api/common/list?pageIndex=0&pageSize=20&targetTable=sys_user&columns=username,real_name&username=test_complete_user" \
  -H "Accept: application/json"
   -H "Authorization: Bearer ${TOKEN}"
