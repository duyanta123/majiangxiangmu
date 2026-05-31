-- AI中心相关表结构创建脚本

-- 1. AI排班记录表
CREATE TABLE IF NOT EXISTS sys_ai_schedule (
    schedule_id BIGINT(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT(20) DEFAULT NULL,
    store_name VARCHAR(100) DEFAULT NULL,
    date VARCHAR(20) DEFAULT NULL COMMENT '排班日期',
    time_slot VARCHAR(50) DEFAULT NULL COMMENT '时段',
    employee_name VARCHAR(50) DEFAULT NULL COMMENT '员工姓名',
    position VARCHAR(50) DEFAULT NULL COMMENT '岗位',
    workload INT(11) DEFAULT 0 COMMENT '预估工作量',
    status CHAR(1) DEFAULT '0' COMMENT '状态 0正常 1休息',
    ai_suggestion VARCHAR(500) DEFAULT NULL COMMENT 'AI建议',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_store_date (store_id, date),
    INDEX idx_time_slot (time_slot)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI排班记录表';

-- 2. AI营销推荐表
CREATE TABLE IF NOT EXISTS sys_ai_recommendation (
    rec_id BIGINT(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    player_id BIGINT(20) DEFAULT NULL,
    player_name VARCHAR(100) DEFAULT NULL,
    player_phone VARCHAR(20) DEFAULT NULL,
    rec_type VARCHAR(50) DEFAULT NULL COMMENT '推荐类型 如:优惠活动、会员升级、积分兑换',
    rec_content VARCHAR(500) DEFAULT NULL COMMENT '推荐内容',
    rec_reason VARCHAR(500) DEFAULT NULL COMMENT '推荐理由',
    status CHAR(1) DEFAULT '0' COMMENT '状态 0未使用 1已使用 2已过期',
    expire_time DATETIME DEFAULT NULL COMMENT '过期时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_player_id (player_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI营销推荐表';

-- 3. AI对话记录表
CREATE TABLE IF NOT EXISTS sys_ai_chat (
    chat_id BIGINT(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    player_id BIGINT(20) DEFAULT NULL,
    player_name VARCHAR(100) DEFAULT NULL,
    player_phone VARCHAR(20) DEFAULT NULL,
    user_input TEXT DEFAULT NULL COMMENT '用户输入',
    ai_response TEXT DEFAULT NULL COMMENT 'AI回复',
    intent VARCHAR(50) DEFAULT NULL COMMENT '意图识别 如:query_reservation/query_store/query_bill',
    chat_type VARCHAR(20) DEFAULT NULL COMMENT '对话类型 如:text/voice',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_player_id (player_id),
    INDEX idx_create_time (create_time),
    INDEX idx_intent (intent)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI对话记录表';
