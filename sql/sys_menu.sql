-- 麻将馆管理系统菜单

-- 先删除已存在的麻将馆相关菜单
DELETE FROM sys_menu WHERE menu_name IN ('玩家管理', '门店管理', '桌台类型', '桌台信息', '预约订单', '消费记录', '门店评价', '会员等级', '会员管理', '积分记录', '数据运营', 'AI中心');

-- 麻将馆管理目录
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time) VALUES
('麻将馆管理', 0, 100, '', 'Layout', 0, 0, 'M', '0', '0', '', 'el-icon-menu', 'admin', NOW(), 'admin', NOW());

-- 麻将馆管理子菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time) VALUES
('玩家管理', 100, 1, '/system/player', 'system/player/index', 1, 0, 'C', '0', '0', 'system:player:list', 'el-icon-user', 'admin', NOW(), 'admin', NOW()),
('门店管理', 100, 2, '/system/store', 'system/store/index', 1, 0, 'C', '0', '0', 'system:store:list', 'el-icon-location', 'admin', NOW(), 'admin', NOW()),
('桌台类型', 100, 3, '/system/tableType', 'system/tableType/index', 1, 0, 'C', '0', '0', 'system:tableType:list', 'el-icon-menu', 'admin', NOW(), 'admin', NOW()),
('桌台信息', 100, 4, '/system/tableInfo', 'system/tableInfo/index', 1, 0, 'C', '0', '0', 'system:tableInfo:list', 'el-icon-grid', 'admin', NOW(), 'admin', NOW()),
('预约订单', 100, 5, '/system/reservation', 'system/reservation/index', 1, 0, 'C', '0', '0', 'system:reservation:list', 'el-icon-calendar', 'admin', NOW(), 'admin', NOW()),
('消费记录', 100, 6, '/system/consume', 'system/consume/index', 1, 0, 'C', '0', '0', 'system:consume:list', 'el-icon-wallet', 'admin', NOW(), 'admin', NOW()),
('门店评价', 100, 7, '/system/review', 'system/review/index', 1, 0, 'C', '0', '0', 'system:review:list', 'el-icon-star-off', 'admin', NOW(), 'admin', NOW()),
('会员等级', 100, 8, '/system/memberLevel', 'system/memberLevel/index', 1, 0, 'C', '0', '0', 'system:memberLevel:list', 'el-icon-trophy', 'admin', NOW(), 'admin', NOW()),
('会员管理', 100, 9, '/system/member', 'system/member/index', 1, 0, 'C', '0', '0', 'system:member:list', 'el-icon-user-solid', 'admin', NOW(), 'admin', NOW()),
('积分记录', 100, 10, '/system/pointsLog', 'system/pointsLog/index', 1, 0, 'C', '0', '0', 'system:pointsLog:list', 'el-icon-coins', 'admin', NOW(), 'admin', NOW()),
('数据运营', 100, 11, '/system/analysis', 'system/analysis/index', 1, 0, 'C', '0', '0', 'system:analysis:dashboard', 'el-icon-data-line', 'admin', NOW(), 'admin', NOW()),
('AI中心', 100, 12, '/system/ai', 'system/ai/index', 1, 0, 'C', '0', '0', 'system:ai:schedule', 'brain', 'admin', NOW(), 'admin', NOW());
