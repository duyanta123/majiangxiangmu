-- 会员等级表
CREATE TABLE IF NOT EXISTS sys_member_level (
    level_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '等级ID',
    level_name VARCHAR(50) NOT NULL COMMENT '等级名称',
    level_code VARCHAR(20) NOT NULL COMMENT '等级编码',
    min_points INT DEFAULT 0 COMMENT '最低积分',
    max_points INT COMMENT '最高积分',
    discount DECIMAL(4,2) DEFAULT 1.00 COMMENT '折扣',
    sort INT DEFAULT 0 COMMENT '排序',
    description VARCHAR(200) COMMENT '描述',
    status CHAR(1) DEFAULT '0' COMMENT '状态',
    del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志',
    create_by VARCHAR(64) COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新者',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员等级表';

-- 会员信息表
CREATE TABLE IF NOT EXISTS sys_member (
    member_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '会员ID',
    player_id BIGINT COMMENT '玩家ID',
    player_name VARCHAR(50) COMMENT '玩家姓名',
    player_phone VARCHAR(20) COMMENT '手机号码',
    level_id BIGINT COMMENT '等级ID',
    level_name VARCHAR(50) COMMENT '等级名称',
    total_points INT DEFAULT 0 COMMENT '累计积分',
    available_points INT DEFAULT 0 COMMENT '可用积分',
    total_consumption DECIMAL(10,2) DEFAULT 0 COMMENT '累计消费',
    card_no VARCHAR(50) COMMENT '会员卡号',
    member_since DATE COMMENT '入会时间',
    expire_time DATE COMMENT '过期时间',
    status CHAR(1) DEFAULT '0' COMMENT '状态',
    del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志',
    create_by VARCHAR(64) COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新者',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(500) COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员信息表';

-- 积分记录表
CREATE TABLE IF NOT EXISTS sys_points_log (
    log_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    member_id BIGINT NOT NULL COMMENT '会员ID',
    player_name VARCHAR(50) COMMENT '玩家姓名',
    player_phone VARCHAR(20) COMMENT '手机号码',
    change_type CHAR(1) NOT NULL COMMENT '变动类型(0增加1扣减)',
    points INT NOT NULL COMMENT '积分数量',
    balance_before INT DEFAULT 0 COMMENT '变动前余额',
    balance_after INT DEFAULT 0 COMMENT '变动后余额',
    source_type VARCHAR(20) COMMENT '来源类型',
    source_id BIGINT COMMENT '来源ID',
    description VARCHAR(200) COMMENT '说明',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分记录表';

-- AI排班表
CREATE TABLE IF NOT EXISTS sys_ai_schedule (
    schedule_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '排班ID',
    store_id BIGINT COMMENT '门店ID',
    store_name VARCHAR(100) COMMENT '门店名称',
    date VARCHAR(20) COMMENT '日期',
    time_slot VARCHAR(20) COMMENT '时段',
    employee_name VARCHAR(50) COMMENT '员工姓名',
    position VARCHAR(50) COMMENT '岗位',
    workload INT COMMENT '工作量',
    status CHAR(1) DEFAULT '0' COMMENT '状态',
    ai_suggestion VARCHAR(500) COMMENT 'AI建议',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI排班表';

-- AI推荐表
CREATE TABLE IF NOT EXISTS sys_ai_recommendation (
    rec_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '推荐ID',
    player_id BIGINT COMMENT '玩家ID',
    player_name VARCHAR(50) COMMENT '玩家姓名',
    player_phone VARCHAR(20) COMMENT '手机号码',
    rec_type VARCHAR(20) COMMENT '推荐类型',
    rec_content VARCHAR(200) COMMENT '推荐内容',
    rec_reason VARCHAR(500) COMMENT '推荐理由',
    status CHAR(1) DEFAULT '0' COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    expire_time DATETIME COMMENT '过期时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI推荐表';

-- AI聊天记录表
CREATE TABLE IF NOT EXISTS sys_ai_chat (
    chat_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '聊天ID',
    player_id BIGINT COMMENT '玩家ID',
    player_name VARCHAR(50) COMMENT '玩家姓名',
    player_phone VARCHAR(20) COMMENT '手机号码',
    user_input TEXT COMMENT '用户输入',
    ai_response TEXT COMMENT 'AI回复',
    intent VARCHAR(50) COMMENT '意图',
    chat_type VARCHAR(20) COMMENT '聊天类型',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI聊天记录表';

-- 初始化会员等级数据
INSERT INTO sys_member_level (level_name, level_code, min_points, max_points, discount, sort, description) VALUES
('普通会员', 'NORMAL', 0, 999, 1.00, 1, '普通会员享原价消费'),
('银卡会员', 'SILVER', 1000, 4999, 0.95, 2, '银卡会员享95折优惠'),
('金卡会员', 'GOLD', 5000, 19999, 0.90, 3, '金卡会员享9折优惠'),
('钻石会员', 'DIAMOND', 20000, NULL, 0.85, 4, '钻石会员享85折优惠');
