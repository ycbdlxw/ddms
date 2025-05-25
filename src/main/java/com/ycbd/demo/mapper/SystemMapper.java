package com.ycbd.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 系统通用数据库操作Mapper接口
 */
@Mapper
public interface SystemMapper {
    /**
     * 获取指定表的主键列名
     * @param table 表名
     * @return 主键列名
     */
    String getPriKeyColumn(@Param("table") String table);
    
    /**
     * 获取表的属性数据
     * @param table 表名
     * @return 属性数据列表
     */
    List<Map<String, Object>> getAttributeData(@Param("table") String table);
    
    /**
     * 分页查询数据(基础版)
     * @param table 表名
     * @param pageIndex 页码
     * @param pageSize 每页大小
     * @param columns 查询列
     * @param joinString 关联语句
     * @param whereStr 条件语句
     * @param sortByAndType 排序语句
     * @param groupByString 分组语句
     * @return 查询结果列表
     */
    List<Map<String, Object>> getItemsData(
        @Param("table") String table,
        @Param("pageIndex") int pageIndex,
        @Param("pageSize") int pageSize,
        @Param("columns") String columns,
        @Param("joinString") String joinString,
        @Param("whereStr") String whereStr,
        @Param("sortByAndType") String sortByAndType,
        @Param("groupByString") String groupByString
    );
    
    /**
     * 分页查询数据(带参数版)
     * @param table 表名
     * @param pageIndex 页码
     * @param pageSize 每页大小
     * @param columns 查询列
     * @param joinString 关联语句
     * @param whereStr 条件语句
     * @param sortByAndType 排序语句
     * @param groupByString 分组语句
     * @param params 额外参数
     * @return 查询结果列表
     */
    List<Map<String, Object>> getItemsData(
        @Param("table") String table,
        @Param("pageIndex") int pageIndex,
        @Param("pageSize") int pageSize,
        @Param("columns") String columns,
        @Param("joinString") String joinString,
        @Param("whereStr") String whereStr,
        @Param("sortByAndType") String sortByAndType,
        @Param("groupByString") String groupByString,
        @Param("params") Map<String, Object> params
    );
    
    /**
     * 插入单条数据
     * @param table 表名
     * @param columns 插入列
     * @param dataMap 数据Map
     * @return 影响行数
     */
    int insertData(
        @Param("table") String table,
        @Param("columns") String columns,
        @Param("dataMap") Map<String, Object> dataMap
    );
    
    /**
     * 更新单条数据
     * @param table 表名
     * @param dataMap 更新数据Map
     * @param primaryKeyName 主键名
     * @param id 主键值
     * @return 影响行数
     */
    int updateData(
        @Param("table") String table,
        @Param("dataMap") Map<String, Object> dataMap,
        @Param("primaryKeyName") String primaryKeyName,
        @Param("id") int id
    );
    
    /**
     * 删除单条数据
     * @param table 表名
     * @param primaryKeyName 主键名
     * @param id 主键值
     * @return 影响行数
     */
    int deleteData(
        @Param("table") String table,
        @Param("primaryKeyName") String primaryKeyName,
        @Param("id") int id
    );

    /**
     * 获取表的字段校验规则
     * @param table 表名
     * @return 校验规则列表
     */
    List<Map<String, Object>> findColumnCheckRules(String table);
    
    /**
     * 获取数据总数
     * @param table 表名
     * @param joinString 关联语句
     * @param whereString 条件语句
     * @return 数据总数
     */
    int getDataCount(
        @Param("table") String table,
        @Param("joinString") String joinString,
        @Param("whereString") String whereString
    );
    
    /**
     * 获取列属性配置
     * @param table 表名
     * @param attributeType 属性类型
     * @return 列属性列表
     */
    List<Map<String, Object>> getColumnAttributes(
        @Param("table") String table,
        @Param("attributeType") String attributeType
    );
    
    /**
     * 批量插入数据
     * @param table 表名
     * @param columns 插入列
     * @param saveData 待保存的数据列表
     * @return 影响行数
     */
    int insertDataBath(
        @Param("table") String table,
        @Param("columns") String columns,
        @Param("saveData") List<Map<String, Object>> saveData
    );
    
    /**
     * 获取分组列
     * @param table 表名
     * @param whereStr 条件语句
     * @return 分组列
     */
    String getGroupColumn(
        @Param("table") String table,
        @Param("whereStr") String whereStr
    );
    
    /**
     * 获取组织机构ID列表
     * @param orgId 组织机构ID
     * @return 组织机构ID字符串
     */
    String getOrgIds(@Param("org_id") String orgId);
    
    /**
     * 获取树形数据
     * @param id 节点ID
     * @return 树形数据
     */
    String getTreedata(@Param("id") String id);
    
    /**
     * 根据列条件统计数据
     * @param table 表名
     * @param params 条件参数
     * @return 统计结果
     */
    int countByColumn(
        @Param("table") String table,
        @Param("params") Map<String, Object> params
    );

    /**
     * 根据条件语句统计数据
     * @param table 表名
     * @param whereString 条件语句
     * @return 统计结果
     */
    int countByWhere(String table, String whereString);

    /**
     * 获取指定数据库的schema信息
     * @param dbsrc 数据源
     * @param schema_name schema名称
     * @return schema信息列表
     */
    List<Map<String, Object>> selectSchema(String dbsrc, String schema_name);
}
