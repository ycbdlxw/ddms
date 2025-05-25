-- 系统日志表
CREATE TABLE IF NOT EXISTS `sys_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `path` varchar(255) NOT NULL COMMENT '请求路径',
  `method` varchar(20) NOT NULL COMMENT '请求方法',
  `params` text COMMENT '请求参数',
  `user_id` bigint(20) COMMENT '操作用户ID',
  `duration` bigint(20) NOT NULL COMMENT '执行时长(毫秒)',
  `status` int(11) NOT NULL COMMENT '响应状态码',
  `error_message` text COMMENT '错误信息',
  `create_time` bigint(20) NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_path` (`path`(191))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统日志表';

-- 添加表属性配置
INSERT INTO `table_attribute` 
(`tableName`, `dbtable`, `sort`, `functions`, `isLoading`, `isAllSelect`, `isRowOpertionFlag`, `isOpertionFlag`, `pageTitle`)
VALUES 
('系统日志', 'sys_log', 'create_time DESC', '系统管理', 1, 1, 0, 0, '系统日志');

-- 添加字段属性配置
INSERT INTO `column_attribute` 
(`dbTableName`, `tableName`, `name`, `pagename`, `IsShowInList`, `searchFlag`, `editFlag`, `showType`, `queryType`, `OrderNo`, `searchOrderNo`, `editOrderNo`, `fieldType`) 
VALUES 
('sys_log', '系统日志', 'path', '请求路径', 1, 1, 0, 'text', 'like', 1, 1, 0, 'varchar'),
('sys_log', '系统日志', 'method', '请求方法', 1, 1, 0, 'text', 'eq', 2, 2, 0, 'varchar'),
('sys_log', '系统日志', 'params', '请求参数', 1, 0, 0, 'textarea', null, 3, 0, 0, 'text'),
('sys_log', '系统日志', 'user_id', '操作用户', 1, 1, 0, 'text', 'eq', 4, 3, 0, 'bigint'),
('sys_log', '系统日志', 'duration', '执行时长', 1, 0, 0, 'number', null, 5, 0, 0, 'bigint'),
('sys_log', '系统日志', 'status', '状态码', 1, 1, 0, 'number', 'eq', 6, 4, 0, 'int'),
('sys_log', '系统日志', 'error_message', '错误信息', 1, 0, 0, 'textarea', null, 7, 0, 0, 'text'),
('sys_log', '系统日志', 'create_time', '创建时间', 1, 1, 0, 'datetime', 'between', 8, 5, 0, 'bigint'); 