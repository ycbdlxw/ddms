-- Active: 1725926521142@@www.ycbd.work@3306@smbservice
SELECT * FROM sys_user WHERE username = 'admin' LIMIT 0, 1


SELECT * FROM sys_user 


select * from column_attribute

select * from column_attribute where `dbTableName`='sys_user' LIMIT 1000

--and name='id'


ALTER TABLE sys_user MODIFY COLUMN create_time BIGINT DEFAULT UNIX_TIMESTAMP()*1000


select * from column_check_property


select * from sys_log


SELECT * FROM role WHERE status = 1 and id in (1,2,3333)


select * from sys_user