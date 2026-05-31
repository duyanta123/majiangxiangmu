-- 桌台预约表
CREATE TABLE IF NOT EXISTS app_reservation (
    reservation_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '预约ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    table_type VARCHAR(50) NOT NULL COMMENT '桌台类型(大厅散台/普通包间/VIP包间)',
    reservation_date DATE NOT NULL COMMENT '预约日期',
    reservation_time VARCHAR(20) NOT NULL COMMENT '预约时间段',
    duration INT DEFAULT 2 COMMENT '预约时长(小时)',
    person_count INT DEFAULT 4 COMMENT '人数',
    contact_name VARCHAR(50) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    remark VARCHAR(500) COMMENT '备注',
    status CHAR(1) DEFAULT '0' COMMENT '状态(0待确认/1已确认/2已完成/3已取消)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志(0正常/1删除)',
    INDEX idx_user_id (user_id),
    INDEX idx_reservation_date (reservation_date)
) ENGINE=InnoDB COMMENT='桌台预约表';

-- 用户积分表
CREATE TABLE IF NOT EXISTS app_user_points (
    point_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '积分ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    total_points INT DEFAULT 0 COMMENT '总积分',
    used_points INT DEFAULT 0 COMMENT '已使用积分',
    available_points INT DEFAULT 0 COMMENT '可用积分',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE INDEX idx_user_id (user_id)
) ENGINE=InnoDB COMMENT='用户积分表';

-- 积分变动记录表
CREATE TABLE IF NOT EXISTS app_point_record (
    record_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    point_change INT NOT NULL COMMENT '积分变动(正数增加/负数减少)',
    change_type VARCHAR(20) NOT NULL COMMENT '变动类型(consume消费/recharge充值/exchange兑换/gift赠送)',
    relation_id BIGINT COMMENT '关联ID',
    remark VARCHAR(200) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB COMMENT='积分变动记录表';

-- 初始化用户积分数据
INSERT INTO app_user_points (user_id, total_points, used_points, available_points) 
SELECT user_id, 0, 0, 0 FROM sys_user WHERE del_flag = '0';
