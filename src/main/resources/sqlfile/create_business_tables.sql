-- 创建远程目录表
CREATE TABLE IF NOT EXISTS remote_directory (
  id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  name varchar(100) NOT NULL COMMENT '目录名称',
  host varchar(100) NOT NULL COMMENT '主机地址',
  share_path varchar(255) NOT NULL COMMENT '共享路径',
  username varchar(100) NOT NULL COMMENT '用户名',
  password varchar(255) NOT NULL COMMENT '密码',
  mount_point varchar(255) NOT NULL COMMENT '挂载点',
  status tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-未挂载,1-已挂载',
  create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_name (name),
  UNIQUE KEY uk_host_path (host, share_path),
  UNIQUE KEY uk_mount_point (mount_point)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='远程目录表';

-- 创建媒体文件表
CREATE TABLE IF NOT EXISTS media_file (
  id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  directory_id bigint(20) NOT NULL COMMENT '目录ID',
  file_path varchar(255) NOT NULL COMMENT '文件路径',
  file_name varchar(255) NOT NULL COMMENT '文件名',
  file_type varchar(50) NOT NULL COMMENT '文件类型:image-图片,video-视频',
  file_size bigint(20) NOT NULL COMMENT '文件大小(字节)',
  md5 varchar(32) NOT NULL COMMENT '文件MD5',
  thumbnail varchar(255) DEFAULT NULL COMMENT '缩略图路径',
  status tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-未同步,1-已同步',
  create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_dir_path_name (directory_id, file_path, file_name),
  KEY idx_directory_id (directory_id),
  KEY idx_file_type (file_type),
  KEY idx_status (status),
  CONSTRAINT fk_media_directory FOREIGN KEY (directory_id) REFERENCES remote_directory (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='媒体文件表';

-- 创建同步任务表
CREATE TABLE IF NOT EXISTS sync_task (
  id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  directory_id bigint(20) NOT NULL COMMENT '目录ID',
  task_type varchar(50) NOT NULL COMMENT '任务类型:scan-扫描,sync-同步',
  status tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-待执行,1-执行中,2-已完成,3-失败',
  total_files int(11) NOT NULL DEFAULT '0' COMMENT '总文件数',
  processed_files int(11) NOT NULL DEFAULT '0' COMMENT '已处理文件数',
  start_time datetime DEFAULT NULL COMMENT '开始时间',
  end_time datetime DEFAULT NULL COMMENT '结束时间',
  error_msg text DEFAULT NULL COMMENT '错误信息',
  create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_directory_id (directory_id),
  KEY idx_status (status),
  KEY idx_create_time (create_time),
  CONSTRAINT fk_task_directory FOREIGN KEY (directory_id) REFERENCES remote_directory (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='同步任务表';

-- 创建用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` varchar(50) NOT NULL COMMENT '用户名',
    `password` varchar(100) NOT NULL COMMENT '密码',
    `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
    `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
    `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
    `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
    `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 创建用户角色表
CREATE TABLE IF NOT EXISTS `sys_role` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `role_name` varchar(50) NOT NULL COMMENT '角色名称',
    `role_code` varchar(50) NOT NULL COMMENT '角色编码',
    `description` varchar(200) DEFAULT NULL COMMENT '角色描述',
    `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

-- 创建用户角色关联表
CREATE TABLE IF NOT EXISTS `sys_user_role` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` bigint(20) NOT NULL COMMENT '用户ID',
    `role_id` bigint(20) NOT NULL COMMENT '角色ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`,`role_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表'; 