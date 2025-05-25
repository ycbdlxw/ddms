-- 插入表属性配置
INSERT INTO table_attribute 
(tableName, dbtable, sort, functions, isRowOpertionFlag, isOpertionFlag, pageTitle, joinStr)
VALUES 
('远程目录', 'remote_directory', 'id DESC', '远程目录管理', 1, 1, '远程目录管理', NULL),
('媒体文件', 'media_file', 'create_time DESC', '媒体文件管理', 1, 1, '媒体文件管理', 
 'LEFT JOIN remote_directory rd ON media_file.directory_id = rd.id'),
('同步任务', 'sync_task', 'create_time DESC', '同步任务管理', 1, 1, '同步任务管理',
 'LEFT JOIN remote_directory rd ON sync_task.directory_id = rd.id');

-- 插入字段属性配置
INSERT INTO column_attribute 
(dbTableName, name, pagename, IsShowInList, searchFlag, editFlag, showType, IsRequired)
VALUES 
-- remote_directory表字段
('remote_directory', 'id', 'ID', 1, 0, 0, 'text', 0),
('remote_directory', 'name', '目录名称', 1, 1, 1, 'text', 1),
('remote_directory', 'host', '主机地址', 1, 1, 1, 'text', 1),
('remote_directory', 'share_path', '共享路径', 1, 1, 1, 'text', 1),
('remote_directory', 'username', '用户名', 1, 1, 1, 'text', 1),
('remote_directory', 'password', '密码', 0, 0, 1, 'password', 1),
('remote_directory', 'mount_point', '挂载点', 1, 1, 1, 'text', 1),
('remote_directory', 'status', '状态', 1, 1, 0, 'select', 0),
('remote_directory', 'create_time', '创建时间', 1, 0, 0, 'datetime', 0),
('remote_directory', 'update_time', '更新时间', 1, 0, 0, 'datetime', 0),

-- media_file表字段
('media_file', 'id', 'ID', 1, 0, 0, 'text', 0),
('media_file', 'directory_id', '所属目录', 1, 1, 1, 'select', 1),
('media_file', 'file_path', '文件路径', 1, 1, 1, 'text', 1),
('media_file', 'file_name', '文件名', 1, 1, 1, 'text', 1),
('media_file', 'file_type', '文件类型', 1, 1, 1, 'select', 1),
('media_file', 'file_size', '文件大小', 1, 1, 0, 'text', 0),
('media_file', 'md5', 'MD5', 1, 1, 0, 'text', 0),
('media_file', 'thumbnail', '缩略图', 1, 0, 0, 'image', 0),
('media_file', 'status', '状态', 1, 1, 0, 'select', 0),
('media_file', 'create_time', '创建时间', 1, 0, 0, 'datetime', 0),
('media_file', 'update_time', '更新时间', 1, 0, 0, 'datetime', 0),

-- sync_task表字段
('sync_task', 'id', 'ID', 1, 0, 0, 'text', 0),
('sync_task', 'directory_id', '目录', 1, 1, 1, 'select', 1),
('sync_task', 'task_type', '任务类型', 1, 1, 1, 'select', 1),
('sync_task', 'status', '状态', 1, 1, 0, 'select', 0),
('sync_task', 'total_files', '总文件数', 1, 0, 0, 'text', 0),
('sync_task', 'processed_files', '已处理文件数', 1, 0, 0, 'text', 0),
('sync_task', 'start_time', '开始时间', 1, 1, 0, 'datetime', 0),
('sync_task', 'end_time', '结束时间', 1, 1, 0, 'datetime', 0),
('sync_task', 'error_msg', '错误信息', 0, 0, 0, 'textarea', 0),
('sync_task', 'create_time', '创建时间', 1, 0, 0, 'datetime', 0),
('sync_task', 'update_time', '更新时间', 1, 0, 0, 'datetime', 0);

-- 插入字段校验配置
INSERT INTO column_check_property
(check_table, target_table, check_column, check_mode, check_order, errorMsg, whereStr)
VALUES
-- remote_directory表校验
('remote_directory', 'remote_directory', 'name', 'isNotExit', 1, '目录名称已存在', 'name = %s'),
('remote_directory', 'remote_directory', 'host,share_path', 'MutiReapeat', 2, '该远程目录已配置', 'host = %s AND share_path = %s'),
('remote_directory', 'remote_directory', 'mount_point', 'isNotExit', 3, '挂载点已被使用', 'mount_point = %s'),

-- media_file表校验
('media_file', 'remote_directory', 'directory_id', 'isExit', 1, '所选目录不存在', 'id = %s'),
('media_file', 'media_file', 'directory_id,file_path,file_name', 'MutiReapeat', 2, '该文件已存在', 'directory_id = %s AND file_path = %s AND file_name = %s'),

-- sync_task表校验
('sync_task', 'remote_directory', 'directory_id', 'isExit', 1, '所选目录不存在', 'id = %s');

-- 插入用户表配置
INSERT INTO `table_attribute` (`tableName`, `dbtable`, `sort`, `mainKey`, `functions`, `addFlag`, `editFlag`, `deleteFlag`, `queryFlag`, `exportFlag`) 
VALUES ('用户管理', 'sys_user', 1, 'id', 'CRUD', 1, 1, 1, 1, 1);

-- 插入用户表字段配置
INSERT INTO `column_attribute` (`dbTableName`, `name`, `pagename`, `fieldType`, `IsShowInList`, `IsShowInQuery`, `IsShowInEdit`, `IsRequired`, `editFlag`, `searchFlag`, `sort`) VALUES 
('sys_user', 'id', 'ID', 'Long', 1, 0, 0, 0, 0, 0, 1),
('sys_user', 'username', '用户名', 'String', 1, 1, 1, 1, 1, 1, 2),
('sys_user', 'password', '密码', 'String', 0, 0, 1, 1, 1, 0, 3),
('sys_user', 'nickname', '昵称', 'String', 1, 1, 1, 0, 1, 1, 4),
('sys_user', 'email', '邮箱', 'String', 1, 1, 1, 0, 1, 1, 5),
('sys_user', 'phone', '手机号', 'String', 1, 1, 1, 0, 1, 1, 6),
('sys_user', 'status', '状态', 'Integer', 1, 1, 1, 1, 1, 1, 7),
('sys_user', 'last_login_time', '最后登录时间', 'DateTime', 1, 0, 0, 0, 0, 0, 8),
('sys_user', 'create_time', '创建时间', 'DateTime', 1, 0, 0, 0, 0, 0, 9),
('sys_user', 'update_time', '更新时间', 'DateTime', 1, 0, 0, 0, 0, 0, 10);

-- 插入角色表配置
INSERT INTO `table_attribute` (`tableName`, `dbtable`, `sort`, `mainKey`, `functions`, `addFlag`, `editFlag`, `deleteFlag`, `queryFlag`, `exportFlag`) 
VALUES ('角色管理', 'sys_role', 2, 'id', 'CRUD', 1, 1, 1, 1, 1);

-- 插入角色表字段配置
INSERT INTO `column_attribute` (`dbTableName`, `name`, `pagename`, `fieldType`, `IsShowInList`, `IsShowInQuery`, `IsShowInEdit`, `IsRequired`, `editFlag`, `searchFlag`, `sort`) VALUES 
('sys_role', 'id', 'ID', 'Long', 1, 0, 0, 0, 0, 0, 1),
('sys_role', 'role_name', '角色名称', 'String', 1, 1, 1, 1, 1, 1, 2),
('sys_role', 'role_code', '角色编码', 'String', 1, 1, 1, 1, 1, 1, 3),
('sys_role', 'description', '角色描述', 'String', 1, 0, 1, 0, 1, 0, 4),
('sys_role', 'status', '状态', 'Integer', 1, 1, 1, 1, 1, 1, 5),
('sys_role', 'create_time', '创建时间', 'DateTime', 1, 0, 0, 0, 0, 0, 6),
('sys_role', 'update_time', '更新时间', 'DateTime', 1, 0, 0, 0, 0, 0, 7);

-- 插入用户角色关联表配置
INSERT INTO `table_attribute` (`tableName`, `dbtable`, `sort`, `mainKey`, `functions`, `addFlag`, `editFlag`, `deleteFlag`, `queryFlag`, `exportFlag`) 
VALUES ('用户角色关联', 'sys_user_role', 3, 'id', 'CRUD', 1, 1, 1, 1, 0);

-- 插入用户角色关联表字段配置
INSERT INTO `column_attribute` (`dbTableName`, `name`, `pagename`, `fieldType`, `IsShowInList`, `IsShowInQuery`, `IsShowInEdit`, `IsRequired`, `editFlag`, `searchFlag`, `sort`) VALUES 
('sys_user_role', 'id', 'ID', 'Long', 1, 0, 0, 0, 0, 0, 1),
('sys_user_role', 'user_id', '用户ID', 'Long', 1, 1, 1, 1, 1, 1, 2),
('sys_user_role', 'role_id', '角色ID', 'Long', 1, 1, 1, 1, 1, 1, 3),
('sys_user_role', 'create_time', '创建时间', 'DateTime', 1, 0, 0, 0, 0, 0, 4);

-- 插入校验规则
INSERT INTO `column_check_property` (`check_table`, `check_column`, `target_table`, `target_column`, `check_type`, `check_order`, `errorMsg`) VALUES
('sys_user', 'username', 'sys_user', 'username', 'isNotExit', 1, '用户名已存在'),
('sys_role', 'role_code', 'sys_role', 'role_code', 'isNotExit', 1, '角色编码已存在'),
('sys_user_role', 'user_id', 'sys_user', 'id', 'isExit', 1, '用户不存在'),
('sys_user_role', 'role_id', 'sys_role', 'id', 'isExit', 2, '角色不存在'),
('sys_user_role', 'user_id,role_id', 'sys_user_role', 'user_id,role_id', 'MutiReapeat', 3, '用户角色关联已存在'); 