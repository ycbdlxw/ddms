package com.ycbd.demo.service;

import cn.hutool.core.map.MapUtil;
import com.ycbd.demo.mapper.SystemMapper;
import com.ycbd.demo.Tools.ResultData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基础数据库操作服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BaseService {
    
    private final SystemMapper systemMapper;
    
    /**
     * 查询列表数据
     */
    public List<Map<String, Object>> queryList(String table, int pageIndex, int pageSize,
            String columns, String joinString, String whereStr, String sortByAndType, String groupByString) {
        return queryList(table, pageIndex, pageSize, columns, joinString, whereStr, sortByAndType, groupByString, null);
    }
    
    /**
     * 查询列表数据(支持参数化查询)
     */
    public List<Map<String, Object>> queryList(String table, int pageIndex, int pageSize,
            String columns, String joinString, String whereStr, String sortByAndType, String groupByString,
            Map<String, Object> params) {
        return systemMapper.getItemsData(table, pageIndex, pageSize, columns, 
                joinString, whereStr, sortByAndType, groupByString, params);
    }
    
    /**
     * 获取总数
     */
    public int count(String table, String joinString, String whereString) {
        return systemMapper.getDataCount(table, joinString, whereString);
    }
    
    /**
     * 获取表属性
     */
    public List<Map<String, Object>> getTableAttributes(String table) {
        return systemMapper.getAttributeData(table);
    }
    
    /**
     * 获取列属性
     */
    public List<Map<String, Object>> getColumnAttributes(String table, String attributeType) {
        return systemMapper.getColumnAttributes(table, attributeType);
    }
    
    /**
     * 保存数据
     */
    public ResultData<Map<String, Object>> save(String table, Map<String, Object> data) {
        try {
            String columns = String.join(",", data.keySet());
            int result = systemMapper.insertData(table, columns, data);
            return ResultData.success(MapUtil.of("result", result));
        } catch (Exception e) {
            log.error("Save data failed", e);
            throw e;
        }
    }
    
    /**
     * 批量保存数据
     */
    public ResultData<Map<String, Object>> saveBatch(String table, String columns, 
            List<Map<String, Object>> saveData) {
        try {
            int result = systemMapper.insertDataBath(table, columns, saveData);
            return ResultData.success(MapUtil.of("result", result));
        } catch (Exception e) {
            log.error("Batch save data failed", e);
            throw e;
        }
    }
    
    /**
     * 更新数据
     */
    public ResultData<Map<String, Object>> update(String table, Map<String, Object> data, int id) {
        try {
            String primaryKey = systemMapper.getPriKeyColumn(table);
            int result = systemMapper.updateData(table, data, primaryKey, id);
            return ResultData.success(MapUtil.of("result", result));
        } catch (Exception e) {
            log.error("Update data failed", e);
            throw e;
        }
    }
    
    /**
     * 删除数据
     */
    public ResultData<Map<String, Object>> delete(String table, int id) {
        try {
            String primaryKey = systemMapper.getPriKeyColumn(table);
            int result = systemMapper.deleteData(table, primaryKey, id);
            return ResultData.success(MapUtil.of("result", result));
        } catch (Exception e) {
            log.error("Delete data failed", e);
            throw e;
        }
    }
    
    /**
     * 获取分组列
     * 
     */
    public String getGroupColumn(String table, String whereStr) {
        return systemMapper.getGroupColumn(table, whereStr);
    }
    
    /**
     * 获取组织机构IDs
     */
    public String getOrgIds(String orgId) {
        return systemMapper.getOrgIds(orgId);
    }
    
    /**
     * 获取树形数据
     */
    public String getTreeData(String id) {
        return systemMapper.getTreedata(id);
    }
    
    /**
     * 获取表配置信息
     * @param tableName 表名
     * @return 表配置信息
     */
    public Map<String, Object> getTableConfig(String tableName) {
        try {
            // 使用现有的 queryList 方法获取数据
            List<Map<String, Object>> results = queryList(
                "table_attribute",  // 表名
                1,                  // pageIndex
                1,                  // pageSize
                "sort, groupby, joinStr, definColumns",  // 需要的字段
                "",                 // 无需 join
                "dbtable = '" + tableName + "'",  // where 条件
                "",                 // 无需排序
                ""                  // 无需分组
            );
            
            return results.isEmpty() ? new HashMap<>() : results.get(0);
        } catch (Exception e) {
            log.error("获取表配置失败: {}", tableName, e);
            return new HashMap<>();
        }
    }

    public List<Map<String, Object>> getTableColumns(String schema_name, String table) {
       return systemMapper.selectSchema(schema_name,table);
    }

   
} 