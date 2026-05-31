package com.ruoyi.system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

@Service
public class AppPointsService {
    
    private static final Logger log = LoggerFactory.getLogger(AppPointsService.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String, Object> getUserPoints(Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            String sql = "SELECT user_id, total_points, used_points, available_points " +
                    "FROM app_user_points WHERE user_id = ?";
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, userId);
            
            if (results.isEmpty()) {
                initUserPoints(userId);
                result.put("userId", userId);
                result.put("totalPoints", 0);
                result.put("usedPoints", 0);
                result.put("availablePoints", 0);
            } else {
                Map<String, Object> row = results.get(0);
                result.put("userId", row.get("user_id"));
                result.put("totalPoints", row.get("total_points") != null ? row.get("total_points") : 0);
                result.put("usedPoints", row.get("used_points") != null ? row.get("used_points") : 0);
                result.put("availablePoints", row.get("available_points") != null ? row.get("available_points") : 0);
            }
            return result;
        } catch (Exception e) {
            log.error("查询用户积分失败: {}", e.getMessage(), e);
            result.put("userId", userId);
            result.put("totalPoints", 0);
            result.put("usedPoints", 0);
            result.put("availablePoints", 0);
            return result;
        }
    }

    public List<Map<String, Object>> getPointRecords(Long userId) {
        try {
            String sql = "SELECT record_id as id, point_change as points, change_type as type, remark, create_time " +
                    "FROM app_point_record WHERE user_id = ? ORDER BY create_time DESC LIMIT 50";
            return jdbcTemplate.queryForList(sql, userId);
        } catch (Exception e) {
            log.error("查询积分记录失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    private void initUserPoints(Long userId) {
        try {
            String sql = "INSERT INTO app_user_points (user_id, total_points, used_points, available_points) " +
                    "VALUES (?, 0, 0, 0)";
            jdbcTemplate.update(sql, userId);
        } catch (Exception e) {
            log.warn("初始化用户积分失败: {}", e.getMessage());
        }
    }
}
