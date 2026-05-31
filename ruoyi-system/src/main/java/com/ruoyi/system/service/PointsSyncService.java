package com.ruoyi.system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class PointsSyncService {
    
    private static final Logger log = LoggerFactory.getLogger(PointsSyncService.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private static final int POINTS_PER_YUAN = 1;

    @Transactional
    public void awardPointsForConsumption(Long playerId, String playerName, Double amount) {
        if (playerId == null || amount == null || amount <= 0) {
            log.warn("无效的积分发放参数 - playerId: {}, amount: {}", playerId, amount);
            return;
        }
        
        int points = (int) (amount * POINTS_PER_YUAN);
        if (points <= 0) {
            log.info("消费金额不足以获得积分 - amount: {}, points: {}", amount, points);
            return;
        }
        
        log.info("发放积分 - playerId: {}, playerName: {}, amount: {}, points: {}", 
                playerId, playerName, amount, points);
        
        try {
            updateAppUserPoints(playerId, points);
            
            insertAppPointRecord(playerId, points, "earn", "消费获得积分");
            
            insertSysPointsLog(playerId, playerName, points, "消费奖励", String.valueOf(amount));
            
            updateMemberLevelIfNeeded(playerId);
            
            log.info("积分发放成功 - playerId: {}, points: {}", playerId, points);
        } catch (Exception e) {
            log.error("发放积分失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    private void updateAppUserPoints(Long playerId, int points) {
        String sql = "UPDATE app_user_points SET " +
                "total_points = total_points + ?, " +
                "available_points = available_points + ?, " +
                "update_time = NOW() " +
                "WHERE user_id = ?";
        
        int updated = jdbcTemplate.update(sql, points, points, playerId);
        
        if (updated == 0) {
            String insertSql = "INSERT INTO app_user_points " +
                    "(user_id, total_points, used_points, available_points, create_time, update_time) " +
                    "VALUES (?, ?, 0, ?, NOW(), NOW())";
            jdbcTemplate.update(insertSql, playerId, points, points);
        }
    }

    private void insertAppPointRecord(Long playerId, int points, String type, String remark) {
        String sql = "INSERT INTO app_point_record " +
                "(user_id, point_change, change_type, remark, create_time) " +
                "VALUES (?, ?, ?, ?, NOW())";
        jdbcTemplate.update(sql, playerId, points, type, remark);
    }

    private void insertSysPointsLog(Long playerId, String playerName, int points, String type, String relationId) {
        String sql = "INSERT INTO sys_points_log " +
                "(player_id, player_name, change_points, balance_points, change_type, " +
                "relation_id, remark, create_by, create_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, 'system', NOW())";
        
        Integer currentBalance = getCurrentBalance(playerId);
        jdbcTemplate.update(sql, 
                playerId, 
                playerName != null ? playerName : "", 
                points, 
                currentBalance + points,
                type,
                relationId,
                "消费获得积分");
    }

    private Integer getCurrentBalance(Long playerId) {
        try {
            String sql = "SELECT total_points FROM app_user_points WHERE user_id = ?";
            return jdbcTemplate.queryForObject(sql, Integer.class, playerId);
        } catch (Exception e) {
            log.warn("获取当前积分余额失败，默认为0: {}", e.getMessage());
            return 0;
        }
    }

    private void updateMemberLevelIfNeeded(Long playerId) {
        try {
            String sql = "SELECT total_points FROM app_user_points WHERE user_id = ?";
            Integer totalPoints = jdbcTemplate.queryForObject(sql, Integer.class, playerId);
            
            if (totalPoints == null) return;
            
            int level = calculateMemberLevel(totalPoints);
            
            String updateSql = "UPDATE sys_member SET member_level = ?, update_time = NOW() WHERE player_id = ?";
            jdbcTemplate.update(updateSql, level, playerId);
            
            log.debug("更新会员等级 - playerId: {}, totalPoints: {}, level: {}", playerId, totalPoints, level);
        } catch (Exception e) {
            log.warn("更新会员等级失败: {}", e.getMessage());
        }
    }

    private int calculateMemberLevel(int points) {
        if (points >= 10000) return 5;
        if (points >= 5000) return 4;
        if (points >= 3000) return 3;
        if (points >= 1000) return 2;
        return 1;
    }

    public void syncAllPointsData() {
        log.info("开始同步所有积分数据...");
        
        try {
            String sql = "SELECT player_id, player_name, total_amount FROM sys_consume WHERE del_flag = '0'";
            List<Map<String, Object>> consumes = jdbcTemplate.queryForList(sql);
            
            for (Map<String, Object> consume : consumes) {
                Long playerId = (Long) consume.get("player_id");
                String playerName = (String) consume.get("player_name");
                Object amountObj = consume.get("total_amount");
                
                if (playerId != null && amountObj != null) {
                    Double amount = ((Number) amountObj).doubleValue();
                    if (amount > 0) {
                        awardPointsForConsumption(playerId, playerName, amount);
                    }
                }
            }
            
            log.info("积分数据同步完成");
        } catch (Exception e) {
            log.error("同步积分数据失败: {}", e.getMessage(), e);
        }
    }

    public void syncUserPoints(Long userId) {
        log.info("同步用户积分数据 - userId: {}", userId);
        
        try {
            String sql = "SELECT SUM(total_amount) as total FROM sys_consume WHERE player_id = ? AND del_flag = '0'";
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, userId);
            
            if (!results.isEmpty() && results.get(0).get("total") != null) {
                Double totalAmount = ((Number) results.get(0).get("total")).doubleValue();
                
                String resetSql = "UPDATE app_user_points SET total_points = 0, used_points = 0, available_points = 0 WHERE user_id = ?";
                jdbcTemplate.update(resetSql, userId);
                
                awardPointsForConsumption(userId, null, totalAmount);
            }
            
            log.info("用户积分同步完成 - userId: {}", userId);
        } catch (Exception e) {
            log.error("同步用户积分失败: {}", e.getMessage(), e);
        }
    }
}