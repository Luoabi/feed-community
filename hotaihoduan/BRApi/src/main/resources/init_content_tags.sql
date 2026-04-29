-- 初始化内容标签关联表
-- 从content表的tags字段（逗号分隔）解析并插入到content_tag表

USE hotai_db;

-- 清空content_tag表（如果需要重新初始化）
-- TRUNCATE TABLE content_tag;

-- 注意：这个脚本需要手动执行，因为MySQL不支持直接在SQL中分割字符串
-- 建议通过Java代码或Python脚本来初始化数据

-- 示例：手动插入一些测试数据
-- INSERT INTO content_tag (content_id, tag_id, tag_name, weight) VALUES
-- (1, 1, '科技', 1.0),
-- (1, 2, 'AI', 0.8),
-- (2, 3, '美食', 1.0);
