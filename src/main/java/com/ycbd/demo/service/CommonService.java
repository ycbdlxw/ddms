package com.ycbd.demo.service;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.ycbd.demo.Tools.ResultData;
import com.ycbd.demo.Tools.Tools;
import com.ycbd.demo.interceptor.ServiceInterceptorAspect;
import com.ycbd.demo.service.validator.DataValidator;
import com.ycbd.demo.service.validator.RuleValidator;
import com.ycbd.demo.service.validator.ValidationResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 通用数据处理服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommonService {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expire-time}")
    private long expireTime;
    private final BaseService baseService;
    private final DataValidator dataValidator;
    private final RuleValidator ruleValidator;
    private final JwtService jwtService;
    
    /**
     * 数据预处理和验证
     */
    private ResultData<Integer> preProcessAndValidate(String targetTable, Map<String, Object> params, boolean isUpdate) {
        try {
            // 获取字段属性
            List<Map<String, Object>> columnAttrs = baseService.getColumnAttributes(targetTable, "edit");
            
            // 数据验证
            ValidationResult validationResult = dataValidator.validate(targetTable, params, columnAttrs);
            if (!validationResult.getErrors().isEmpty()) {
                return ResultData.fail(400, String.join("; ", validationResult.getErrors()));
            }
            
            // 业务规则验证
            List<String> ruleErrors = ruleValidator.validate(targetTable, params);
            if (!ruleErrors.isEmpty()) {
                return ResultData.fail(400, String.join("; ", ruleErrors));
            }
            
            // 处理密码字段和移除不可编辑字段
            for (Map<String, Object> attr : columnAttrs) {
                String fieldName = MapUtil.getStr(attr, "name");
                String showType = MapUtil.getStr(attr, "showType");
                
                // 跳过不可编辑字段
                if (!isUpdate && MapUtil.getInt(attr, "editFlag", 0) != 1) {
                    params.remove(fieldName);
                    continue;
                }
                
                // 处理密码字段
                if ("password".equals(showType) && params.containsKey(fieldName)) {
                    String password = MapUtil.getStr(params, fieldName);
                    if (StrUtil.isNotEmpty(password)) {
                        params.put(fieldName, BCrypt.hashpw(password, BCrypt.gensalt()));
                    } else if (isUpdate) {
                        params.remove(fieldName);
                    }
                }
            }
            
            // 设置基础字段
            long currentTime = System.currentTimeMillis();
            if (isUpdate) {
                if (columnAttrs.stream().anyMatch(attr -> "update_time".equals(MapUtil.getStr(attr, "prop")))) {
                    params.put("update_time", currentTime);
                }
                if (columnAttrs.stream().anyMatch(attr -> "update_by".equals(MapUtil.getStr(attr, "prop")))) {
                    params.put("update_by", getCurrentUserId());
                }
            } else {
                if (columnAttrs.stream().anyMatch(attr -> "create_time".equals(MapUtil.getStr(attr, "prop")))) {
                    params.put("create_time", currentTime);
                }
                if (columnAttrs.stream().anyMatch(attr -> "update_time".equals(MapUtil.getStr(attr, "prop")))) {
                    params.put("update_time", currentTime);
                }
                if (columnAttrs.stream().anyMatch(attr -> "create_by".equals(MapUtil.getStr(attr, "prop")))) {
                    params.put("create_by", getCurrentUserId());
                }
                if (columnAttrs.stream().anyMatch(attr -> "update_by".equals(MapUtil.getStr(attr, "prop")))) {
                    params.put("update_by", getCurrentUserId());
                }
            }
            
            
            return ResultData.success(1);
        } catch (Exception e) {
            log.error("数据预处理失败", e);
            return ResultData.fail(500, "数据预处理失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取数据列表
     */
    public ResultData<Map<String, Object>> getList(int pageIndex, int pageSize, 
            Map<String, Object> params) {
        try {
            String targetTable = MapUtil.getStr(params, "targetTable");
            if (StrUtil.isEmpty(targetTable)) {
                return ResultData.fail(400, "targetTable不能为空");
            }
            
            // 获取表配置
            Map<String, Object> tableConfig = baseService.getTableConfig(targetTable);
            
            // 获取配置参数
            String joinString = MapUtil.getStr(params, "joinString", 
                MapUtil.getStr(tableConfig, "joinStr"));
            String sortByAndType = MapUtil.getStr(params, "sortByAndType", 
                MapUtil.getStr(tableConfig, "sort", "create_time DESC"));
            String groupByString = MapUtil.getStr(params, "groupByString", 
                MapUtil.getStr(tableConfig, "groupby"));
            
            // 使用 SqlWhereBuilder 构建查询条件
           List<Map<String,Object>> attributeList= baseService.getColumnAttributes(targetTable,"search");
           String columns = MapUtil.getStr(params, "columns", "*");
            String whereStr = SqlWhereBuilder.build(targetTable, params, attributeList, false).toString();
            
            // 获取数据
            List<Map<String, Object>> items = baseService.queryList(
                targetTable,
                pageIndex,
                pageSize,
                columns,
                joinString,
                whereStr,
                sortByAndType,
                groupByString
            );
            
            // 获取总数
            int total = baseService.count(targetTable, joinString, whereStr);
            
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("items", items);
            resultMap.put("total", total);
            return ResultData.success(resultMap);
                
        } catch (Exception e) {
            log.error("获取数据列表失败", e);
            return ResultData.fail(500, "获取数据失败：" + e.getMessage());
        }
    }
    
    /**
     * 保存或更新数据
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultData<Map<String, Object>> saveData(Map<String, Object> params) {
        try {
            String targetTable = MapUtil.getStr(params, "targetTable");
            if (StrUtil.isEmpty(targetTable)) {
                return ResultData.fail(400, "targetTable不能为空");
            }
            
            // 判断是新增还是更新
            Integer id = MapUtil.getInt(params, "id");
            boolean isUpdate = id != null && id > 0;
            
            // 数据预处理和验证
            ResultData<Integer> preResult = preProcessAndValidate(targetTable, params, isUpdate);
            if (preResult.getCode()!=200) {
                return ResultData.fail(preResult.getCode(), preResult.getMessage());
            }
            
            // 移除非数据字段
            params.remove("targetTable");
            
            // 执行保存或更新
            if (isUpdate) {
                return baseService.update(targetTable, params, id);
            } else {
                return baseService.save(targetTable, params);
            }
        } catch (Exception e) {
            log.error("保存数据失败", e);
            return ResultData.fail(500, "保存数据失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除数据
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultData<Map<String, Object>> deleteData(Map<String, Object> params) {
        try {
            // 参数校验
            String targetTable = MapUtil.getStr(params, "targetTable");
            Integer id = MapUtil.getInt(params, "id");
            if (StrUtil.isEmpty(targetTable) || id == null) {
                return ResultData.fail(400, "targetTable和id不能为空");
            }
            
            return baseService.delete(targetTable, id);
            
        } catch (Exception e) {
            log.error("Delete data failed", e);
            return ResultData.fail(500, "删除数据失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取数据详情
     */
    public ResultData<Map<String, Object>> getDetail(Map<String, Object> params) {
        try {
            // 参数校验
            String targetTable = MapUtil.getStr(params, "targetTable");
            Integer id = MapUtil.getInt(params, "id");
            if (StrUtil.isEmpty(targetTable) || id == null) {
                return ResultData.fail(400, "targetTable和id不能为空");
            }
            
            StringBuilder whereBuilder = new StringBuilder();
            whereBuilder.append(targetTable).append(".id = ").append(id);
            
            List<Map<String, Object>> items = baseService.queryList(
                targetTable,
                1,
                1,
                "*",
                null,
                whereBuilder.toString(),
                null,
                null
            );
            
            if (items.isEmpty()) {
                return ResultData.fail(404, "数据不存在");
            }
            
            return ResultData.success(items.get(0));
            
        } catch (Exception e) {
            log.error("Get detail failed", e);
            return ResultData.fail(500, "获取详情失败：" + e.getMessage());
        }
    }
        /**
     * 通过表名，进行数据库表的列属性初始化
     * 
     * @param table
     * @param tablename
     * @param schema_name
     * @return
     */
    public ResultData<Map<String, Object>> initColumnAttribule(String table, String tablename, String schema_name) {
        List<Map<String, Object>> colsresult = baseService.getTableColumns(schema_name, table);
        List<Map<String, Object>> saveDataList = new ArrayList<>();

        AtomicInteger index = new AtomicInteger(0);
        for (Map<String, Object> it : colsresult) {
            String columnName = MapUtil.getStr(it, "COLUMN_NAME");
            if(baseService.count(table, "", "dbtable='"+table+"' and name='"+columnName+"'")>0)
            continue;
            
            String dataType = MapUtil.getStr(it, "DATA_TYPE");
            Integer maxLength = MapUtil.getInt(it, "CHARACTER_MAXIMUM_LENGTH");
            if (maxLength == null)
                maxLength = MapUtil.getInt(it, "NUMERIC_PRECISION");

            String comment = MapUtil.getStr(it, "COLUMN_COMMENT").replaceAll("，", ",");
            String columnKey = MapUtil.getStr(it, "COLUMN_KEY");
            String columnDefault = MapUtil.getStr(it, "COLUMN_DEFAULT");
            String isNullable = MapUtil.getStr(it, "IS_NULLABLE");
            int orderNo = index.getAndIncrement();
            Map<String, Object> attributeMap = new HashMap<>();
            attributeMap.put("orderNo", orderNo);
            attributeMap.put("name", columnName);
            if (StrUtil.isNotEmpty(columnDefault)) {
                attributeMap.put("defaultValue", columnDefault);
            } else
                attributeMap.put("defaultValue", null);
            attributeMap.put("isPri", columnKey == "PRI" ? 1 : 0);

            attributeMap.put("dbTableName", table);
            attributeMap.put("tableName", tablename);
            attributeMap.put("isRequired", isNullable == "YES" ? 1 : 0);

            String colRemarks = comment;
            attributeMap.put("fieldType", dataType);
            attributeMap.put("len", maxLength == null ? 12 : maxLength);
            if (!colRemarks.contains(",")) {
                attributeMap.put("options", "");
            }
            attributeMap.put("pagename", colRemarks.split(",")[0]);
            colRemarks = StrUtil.subAfter(colRemarks, ",", false).replace(",", "|");
            attributeMap.put("options", colRemarks);
            if (colRemarks.length() > 2 && colRemarks.length() < 4)
                attributeMap.put("showType", "radio");
            else
                attributeMap.put("showType", "input");
            saveDataList.add(attributeMap);
        }

        try {
            if (saveDataList.size() > 0) {
               return baseService.saveBatch("column_attribute", Tools.getMapKeysToColumns(saveDataList.get(0)),saveDataList);
            }
            Map<String,Object> result=new HashMap<>();
            result.put("保存的数据为0:", saveDataList.size());
            return ResultData.success(result);
        } catch (Exception e) {

            System.out.println("error: " + e.getMessage());
            return ResultData.fail(500, "属性数据保存失败：" + e.getMessage());
        }

     
       
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        Integer userId = ServiceInterceptorAspect.getCurrentUserId();
        return userId != null ? userId.longValue() : null;
    }
    
    /**
     * 用户登录
     */
    public ResultData<Map<String, Object>> login(Map<String, Object> params) {
        try {
            String username = MapUtil.getStr(params, "username");
            String password = MapUtil.getStr(params, "password");
            
            // 参数校验
            if (StrUtil.isEmpty(username) || StrUtil.isEmpty(password)) {
                return ResultData.fail(400, "用户名和密码不能为空");
            }
            
            // 查询用户
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("username", username);
            List<Map<String, Object>> users = baseService.queryList(
                "sys_user",
                0,
                1,
                "*",
                null,
                "username = #{params.username}",
                null,
                null,
                queryParams
            );
            
            // 用户不存在
            if (users.isEmpty()) {
                log.warn("登录失败: 用户[{}]不存在", username);
                return ResultData.fail(401, "用户不存在");
            }
            
            Map<String, Object> user = users.get(0);
            String hashedPassword = MapUtil.getStr(user, "password");
            
            // 验证密码
            if (!BCrypt.checkpw(password, hashedPassword)) {
                log.warn("登录失败: 用户[{}]密码错误", username);
                return ResultData.fail(402, "密码错误");
            }
            
            // 移除敏感信息
            user.remove("password");
            
            // 更新最后登录时间
            Map<String, Object> updateParams = new HashMap<>();
            updateParams.put("last_login_time", System.currentTimeMillis());
            baseService.update("sys_user", updateParams, MapUtil.getInt(user, "id"));
            
            // 生成token
            Map<String, Object> payload = new HashMap<>();
            payload.put("userId", user.get("id"));
            payload.put("username", user.get("username"));
            payload.put("orgId", user.get("org_id"));
            payload.put("roles", user.get("roles"));
            
            String token = jwtService.generateToken(payload);
            
            // 添加token到返回结果
            user.put("token", token);
            
            log.info("用户[{}]登录成功", username);
            return ResultData.success(user);
            
        } catch (Exception e) {
            log.error("登录失败", e);
            return ResultData.fail(500, "登录失败：" + e.getMessage());
        }
    }
} 