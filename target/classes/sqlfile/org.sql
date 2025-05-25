-- 创建机构表
CREATE TABLE IF NOT EXISTS `org` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) NOT NULL COMMENT '机构名称',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态,1-正常 0-禁用',
  `create_by` bigint(20) COMMENT '创建人ID',
  `update_by` bigint(20) COMMENT '更新人ID',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='机构表';

-- 初始化机构数据
INSERT INTO `org` (`name`, `status`) VALUES 
('系统', 1),
('业务', 1);




-- 添加表属性配置
INSERT INTO `table_attribute` 
(`tableName`, `dbtable`, `sort`, `functions`, `isLoading`, `isAllSelect`, `isRowOpertionFlag`, `isOpertionFlag`, `pageTitle`)
VALUES 
('机构', 'org', 'id DESC', '机构管理', 1, 1, 1, 1, '机构管理'),
('角色', 'role', 'id DESC', '角色管理', 1, 1, 1, 1, '角色管理');

-- 添加字段属性配置
INSERT INTO `column_attribute` 
(`dbTableName`, `tableName`, `name`, `pagename`, `IsShowInList`, `searchFlag`, `editFlag`, `showType`, `queryType`, `OrderNo`, `searchOrderNo`, `editOrderNo`, `fieldType`) 
VALUES 
('org', '机构', 'id', '主键ID', 1, 0, 0, 'text', null, 0, 0, 0, 'bigint'),
('org', '机构', 'name', '机构名称', 1, 1, 1, 'text', 'like', 1, 1, 1, 'varchar'),
('org', '机构', 'status', '状态', 1, 1, 1, 'switch', 'eq', 2, 2, 2, 'tinyint'),
('org', '机构', 'create_time', '创建时间', 1, 1, 0, 'datetime', 'between', 3, 3, 0, 'bigint'),
('org', '机构', 'update_time', '更新时间', 1, 0, 0, 'datetime', null, 4, 0, 0, 'bigint'),
('role', '角色', 'id', '主键ID', 1, 0, 0, 'text', null, 0, 0, 0, 'bigint'),
('role', '角色', 'name', '角色名称', 1, 1, 1, 'text', 'like', 1, 1, 1, 'varchar'),
('role', '角色', 'status', '状态', 1, 1, 1, 'switch', 'eq', 2, 2, 2, 'tinyint'),
('role', '角色', 'create_time', '创建时间', 1, 1, 0, 'datetime', 'between', 3, 3, 0, 'bigint'),
('role', '角色', 'update_time', '更新时间', 1, 0, 0, 'datetime', null, 4, 0, 0, 'bigint');