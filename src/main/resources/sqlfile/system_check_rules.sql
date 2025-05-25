CREATE TABLE IF NOT EXISTS `column_check_property` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `check_table` VARCHAR(50) NOT NULL COMMENT '检查表名',
  `target_table` VARCHAR(50) NOT NULL COMMENT '目标表名',
  `check_column` VARCHAR(50) NOT NULL COMMENT '列名',
  `check_mode` VARCHAR(50) NOT NULL COMMENT '检查模式',
  `check_order` INT NOT NULL DEFAULT 0 COMMENT '校验顺序',
  `status` TINYINT(4) NOT NULL DEFAULT '1' COMMENT '状态,1-有效 0-无效',
  `errorMsg` VARCHAR(255) NOT NULL COMMENT '错误信息',
  `whereStr` VARCHAR(255) DEFAULT NULL COMMENT '附加条件',
  `params` JSON DEFAULT NULL COMMENT '多参数',
  PRIMARY KEY (`id`),
  KEY `idx_check_table` (`check_table`),
  KEY `idx_check_column` (`check_column`),
  KEY `idx_check_order` (`check_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='列检查属性表';


-- 新增加一个字段：target_table ，用来处理保存中列值是否存在对应表，



-- 系统用户表的校验规则
TRUNCATE TABLE `column_check_property`;

--drop TABLE column_check_property

select * from column_check_property



-- 系统用户表的校验规则
INSERT INTO `column_check_property` 
(`check_table`, `target_table`, `check_column`, `check_mode`, `check_order`, `status`, `errorMsg`, `whereStr`, `params`) 
VALUES 
('sys_user', 'sys_user', 'username', 'isNotExit', 1, 1, '用户名已存在', 'status = 1 and username=%s', null),
('sys_user', 'sys_user', 'phone', 'isNotExit', 2, 1, '手机号已被使用', 'status = 1 and phone=%s', null),
('sys_user', 'sys_user', 'email', 'isNotExit', 3, 1, '邮箱已被使用', 'status = 1 and email=%s', null),
('sys_user', 'org', 'org_id', 'isExit', 4, 1, '所选组织机构不存在', 'status = 1 and id=%s', null),
('sys_user', 'role', 'role_ids', 'isExit', 5, 1, '角色ID不存在', 'status = 1 and id in (%s)', null),
('sys_user', 'sys_user', 'age', 'isRang', 6, 1, '年龄必须在18到60之间', null, '{"min": 18, "max": 60}');

-- 校验业务逻辑：
-- 1. 根据check_table获取需要校验的记录，如sys_user表的所有校验规则
-- 2. 根据check_column获取保存时的值，如username、phone等字段的值
-- 3. 根据whereStr条件对target_table进行查询，如查询sys_user表中status=1的记录
-- 4. 根据check_mode判断校验结果:
--    - isNotExit: 确保值在目标表中不存在，用于唯一性校验(如用户名不重复)
--    - isExit: 确保值在目标表中存在，用于外键关联校验(如org_id必须存在)
--    - isRang: 确保数值在指定范围内，使用params中的min/max(如年龄范围)
--    - MutiReapeat: 确保多个字段组合不重复
