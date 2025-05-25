-- 创建角色表
CREATE TABLE IF NOT EXISTS `role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) NOT NULL COMMENT '角色名称',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态,1-正常 0-禁用',
   `create_by` bigint(20) COMMENT '创建人ID',
  `update_by` bigint(20) COMMENT '更新人ID',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 初始化角色数据
INSERT INTO `role` (`name`, `status`) VALUES 
('系统管理员', 1),
('机构管理员', 1),
('普通用户', 1),
('游客', 1);