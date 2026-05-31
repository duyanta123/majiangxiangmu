-- 修复麻将馆管理系统菜单配置

-- 1. 删除所有麻将馆相关的菜单（包括目录和子菜单）
DELETE FROM sys_menu WHERE menu_name IN (
    '麻将馆管理', '玩家管理', '门店管理', '桌台类型', '桌台信息', 
    '预约订单', '消费记录', '门店评价', '会员等级', '会员管理', 
    '积分记录', '数据运营', 'AI中心'
);

-- 2. 重新插入菜单，先插入目录
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time) VALUES
('麻将馆管理', 0, 100, '', 'Layout', 0, 0, 'M', '0', '0', '', 'el-icon-menu', 'admin', NOW(), 'admin', NOW());

-- 获取刚插入的麻将馆管理目录的menu_id（假设是最后插入的ID）
-- 下面使用parent_id为新插入的目录ID

-- 3. 插入子菜单（使用LAST_INSERT_ID()获取父菜单ID）
SET @parentId = LAST_INSERT_ID();

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time) VALUES
('玩家管理', @parentId, 1, '/system/player', 'system/player/index', 1, 0, 'C', '0', '0', 'system:player:list', 'el-icon-user', 'admin', NOW(), 'admin', NOW()),
('门店管理', @parentId, 2, '/system/store', 'system/store/index', 1, 0, 'C', '0', '0', 'system:store:list', 'el-icon-location', 'admin', NOW(), 'admin', NOW()),
('桌台类型', @parentId, 3, '/system/tableType', 'system/tableType/index', 1, 0, 'C', '0', '0', 'system:tableType:list', 'el-icon-menu', 'admin', NOW(), 'admin', NOW()),
('桌台信息', @parentId, 4, '/system/tableInfo', 'system/tableInfo/index', 1, 0, 'C', '0', '0', 'system:tableInfo:list', 'el-icon-grid', 'admin', NOW(), 'admin', NOW()),
('预约订单', @parentId, 5, '/system/reservation', 'system/reservation/index', 1, 0, 'C', '0', '0', 'system:reservation:list', 'el-icon-calendar', 'admin', NOW(), 'admin', NOW()),
('消费记录', @parentId, 6, '/system/consume', 'system/consume/index', 1, 0, 'C', '0', '0', 'system:consume:list', 'el-icon-wallet', 'admin', NOW(), 'admin', NOW()),
('门店评价', @parentId, 7, '/system/review', 'system/review/index', 1, 0, 'C', '0', '0', 'system:review:list', 'el-icon-star-off', 'admin', NOW(), 'admin', NOW()),
('会员等级', @parentId, 8, '/system/memberLevel', 'system/memberLevel/index', 1, 0, 'C', '0', '0', 'system:memberLevel:list', 'el-icon-trophy', 'admin', NOW(), 'admin', NOW()),
('会员管理', @parentId, 9, '/system/member', 'system/member/index', 1, 0, 'C', '0', '0', 'system:member:list', 'el-icon-user-solid', 'admin', NOW(), 'admin', NOW()),
('积分记录', @parentId, 10, '/system/pointsLog', 'system/pointsLog/index', 1, 0, 'C', '0', '0', 'system:pointsLog:list', 'el-icon-coins', 'admin', NOW(), 'admin', NOW()),
('数据运营', @parentId, 11, '/system/analysis', 'system/analysis/index', 1, 0, 'C', '0', '0', 'system:analysis:dashboard', 'el-icon-data-line', 'admin', NOW(), 'admin', NOW()),
('AI中心', @parentId, 12, '/system/ai', 'system/ai/index', 1, 0, 'C', '0', '0', 'system:ai:schedule', 'el-icon-brain', 'admin', NOW(), 'admin', NOW());
