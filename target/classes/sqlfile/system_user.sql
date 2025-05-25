-- 系统用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `real_name` varchar(50) COMMENT '真实姓名',
  `phone` varchar(20) COMMENT '手机号',
  `email` varchar(100) COMMENT '邮箱',
  `avatar` varchar(255) COMMENT '头像URL',
  `org_id` bigint(20) COMMENT '组织机构ID',
  `role_ids` varchar(255) COMMENT '角色IDs',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态：1-正常 0-禁用',
  `last_login_time` bigint(20) COMMENT '最后登录时间',
  `last_login_ip` varchar(50) COMMENT '最后登录IP',
  `create_time` bigint(20) NOT NULL DEFAULT UNIX_TIMESTAMP()*1000 COMMENT '创建时间',
  `update_time` bigint(20) COMMENT '更新时间',
  `create_by` bigint(20) COMMENT '创建人ID',
  `update_by` bigint(20) COMMENT '更新人ID',
  `remark` varchar(500) COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_org_id` (`org_id`),
  KEY `idx_status` (`status`),
  KEY `idx_phone` (`phone`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 添加表属性配置
INSERT INTO `table_attribute` 
(`tableName`, `dbtable`, `sort`, `functions`, `isLoading`, `isAllSelect`, `isRowOpertionFlag`, `isOpertionFlag`, `pageTitle`)
VALUES 
('系统用户', 'sys_user', 'create_time DESC', '系统管理', 1, 1, 1, 1, '用户管理');

-- 添加字段属性配置
INSERT INTO `column_attribute` 
(`dbTableName`, `tableName`, `name`, `pagename`, `IsShowInList`, `searchFlag`, `editFlag`, `showType`, `queryType`, `OrderNo`, `searchOrderNo`, `editOrderNo`, `fieldType`) 
VALUES 
('sys_user', '系统用户', 'id', '主键ID', 1, 0, 0, 'text', null, 0, 0, 0, 'bigint'),
('sys_user', '系统用户', 'username', '用户名', 1, 1, 1, 'text', 'like', 1, 1, 1, 'varchar'),
('sys_user', '系统用户', 'password', '密码', 0, 0, 1, 'password', null, 2, 0, 2, 'varchar'),
('sys_user', '系统用户', 'real_name', '真实姓名', 1, 1, 1, 'text', 'like', 3, 2, 3, 'varchar'),
('sys_user', '系统用户', 'phone', '手机号', 1, 1, 1, 'text', 'eq', 4, 3, 4, 'varchar'),
('sys_user', '系统用户', 'email', '邮箱', 1, 1, 1, 'text', 'like', 5, 4, 5, 'varchar'),
('sys_user', '系统用户', 'avatar', '头像', 1, 0, 1, 'image', null, 6, 0, 6, 'varchar'),
('sys_user', '系统用户', 'org_id', '所属机构', 1, 1, 1, 'select', 'eq', 7, 5, 7, 'bigint'),
('sys_user', '系统用户', 'role_ids', '角色', 1, 1, 1, 'multiSelect', 'in', 8, 6, 8, 'varchar'),
('sys_user', '系统用户', 'status', '状态', 1, 1, 1, 'switch', 'eq', 9, 7, 9, 'tinyint'),
('sys_user', '系统用户', 'last_login_time', '最后登录时间', 1, 0, 0, 'datetime', null, 10, 0, 0, 'bigint'),
('sys_user', '系统用户', 'last_login_ip', '最后登录IP', 1, 0, 0, 'text', null, 11, 0, 0, 'varchar'),
('sys_user', '系统用户', 'create_time', '创建时间', 1, 1, 0, 'datetime', 'between', 12, 8, 0, 'bigint'),
('sys_user', '系统用户', 'update_time', '更新时间', 1, 0, 0, 'datetime', null, 13, 0, 0, 'bigint'),
('sys_user', '系统用户', 'remark', '备注', 1, 0, 1, 'textarea', null, 14, 0, 10, 'varchar');

-- 添加初始管理员账号
ALTER TABLE `sys_user` ADD COLUMN `age` int COMMENT '年龄';

INSERT INTO `column_attribute`
(`dbTableName`, `tableName`, `name`, `pagename`, `IsShowInList`, `searchFlag`, `editFlag`, `showType`, `queryType`, `OrderNo`, `searchOrderNo`, `editOrderNo`, `fieldType`)
VALUES
('sys_user', '系统用户', 'age', '年龄', 1, 1, 1, 'number', 'eq', 15, 9, 11, 'int');

INSERT INTO `sys_user`
(`username`, `password`, `real_name`, `status`, `create_time`, `update_time`)
VALUES 
('admin', '$2a$10$SnIoHBHxHWNGkwxHn0OkAu1E0FgqWRi8WQXP0tP6DcME4wR6lQy2q', '系统管理员', 1, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000); 





INSERT INTO `column_attribute` 
(`dbTableName`, `tableName`, `name`, `pagename`, `IsShowInList`, `searchFlag`, `editFlag`, `showType`, `queryType`, `OrderNo`, `searchOrderNo`, `editOrderNo`, `fieldType`) 
VALUES 
('sys_user', '系统用户', 'id', '主键ID', 1, 0, 0, 'text', null, 0, 0, 0, 'bigint')