-- 添加个人简介字段到用户表
-- 执行日期: 2026-04-10

ALTER TABLE sys_user 
ADD COLUMN bio VARCHAR(500) COMMENT '个人简介' AFTER gender;
