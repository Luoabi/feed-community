-- 添加热度分数字段到content表
USE hotai_db;

ALTER TABLE content ADD COLUMN hot_score DOUBLE DEFAULT 0 COMMENT '热度分数';
ALTER TABLE content ADD INDEX idx_hot_score (hot_score DESC);
