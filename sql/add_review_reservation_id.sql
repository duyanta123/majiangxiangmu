-- 为sys_review表添加reservation_id字段
-- 如果字段已存在会报错，可忽略

ALTER TABLE sys_review ADD COLUMN reservation_id BIGINT COMMENT '预约ID' AFTER player_name;

-- 添加索引
ALTER TABLE sys_review ADD INDEX idx_reservation_id (reservation_id);

-- 添加备注：评论 2026-05-30
