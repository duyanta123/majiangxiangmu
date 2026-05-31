package com.ruoyi.system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PlayerConsumptionQueryService {
    
    private static final Logger log = LoggerFactory.getLogger(PlayerConsumptionQueryService.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public static class ConsumptionResult {
        public boolean success;
        public String message;
        public Map<String, Object> data;
        public boolean playerExists;
        
        public ConsumptionResult(boolean success, String message) {
            this.success = success;
            this.message = message;
            this.data = new HashMap<>();
            this.playerExists = true;
        }
    }
    
    public ConsumptionResult queryPlayerConsumption(String question) {
        if (question == null || question.trim().isEmpty()) {
            return new ConsumptionResult(false, "问题不能为空");
        }
        
        Long playerId = extractPlayerId(question);
        String playerName = extractPlayerName(question);
        
        if (playerId == null && playerName == null) {
            return new ConsumptionResult(false, "无法识别玩家信息，请提供玩家编号或姓名");
        }
        
        try {
            String playerNameFromDb = null;
            boolean exists = false;
            
            if (playerId != null) {
                exists = checkPlayerExistsById(playerId);
                if (exists) {
                    playerNameFromDb = getPlayerNameById(playerId);
                }
            } else {
                Long foundPlayerId = findPlayerIdByName(playerName);
                if (foundPlayerId != null) {
                    playerId = foundPlayerId;
                    exists = true;
                    playerNameFromDb = playerName;
                }
            }
            
            if (!exists) {
                ConsumptionResult result = new ConsumptionResult(false, getPlayerNotFoundMessage(playerId, playerName));
                result.playerExists = false;
                result.data.put("playerId", playerId);
                result.data.put("playerName", playerName);
                result.data.put("totalConsumption", null);
                result.data.put("consumptionCount", 0);
                result.data.put("lastConsumptionTime", null);
                return result;
            }
            
            String finalPlayerName = playerNameFromDb != null ? playerNameFromDb : playerName;
            
            String sql = "SELECT c.player_id, c.player_name, " +
                  "SUM(c.total_amount) as total_consumption, " +
                  "COUNT(*) as consumption_count, " +
                  "MAX(c.create_time) as last_consumption_time " +
                  "FROM sys_consume c " +
                  "WHERE c.player_id = ? AND c.del_flag = '0' " +
                  "GROUP BY c.player_id, c.player_name";
            
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, playerId);
            
            if (results == null || results.isEmpty()) {
                ConsumptionResult result = new ConsumptionResult(true, "查询成功");
                result.playerExists = true;
                result.data.put("playerId", playerId);
                result.data.put("playerName", finalPlayerName);
                result.data.put("totalConsumption", null);
                result.data.put("consumptionCount", 0);
                result.data.put("lastConsumptionTime", null);
                result.data.put("hasConsumption", false);
                return result;
            }
            
            Map<String, Object> row = results.get(0);
            Object totalAmount = row.get("total_consumption");
            Object count = row.get("consumption_count");
            
            ConsumptionResult result = new ConsumptionResult(true, "查询成功");
            result.playerExists = true;
            result.data.put("playerId", row.get("player_id"));
            result.data.put("playerName", row.get("player_name") != null ? row.get("player_name") : finalPlayerName);
            result.data.put("totalConsumption", totalAmount);
            result.data.put("consumptionCount", count != null ? count : 0);
            result.data.put("lastConsumptionTime", row.get("last_consumption_time"));
            result.data.put("hasConsumption", totalAmount != null && ((Number) totalAmount).doubleValue() > 0);
            
            log.info("查询玩家消费成功 - playerId: {}, totalConsumption: {}", 
                    row.get("player_id"), totalAmount);
            
            return result;
            
        } catch (Exception e) {
            log.error("查询玩家消费失败: {}", e.getMessage(), e);
            return new ConsumptionResult(false, "查询失败: " + e.getMessage());
        }
    }
    
    private boolean checkPlayerExistsById(Long playerId) {
        try {
            String sql = "SELECT COUNT(*) FROM sys_player WHERE player_id = ? AND del_flag = '0'";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, playerId);
            return count != null && count > 0;
        } catch (Exception e) {
            log.warn("检查玩家存在性失败，尝试从消费记录表查询: {}", e.getMessage());
            String sql = "SELECT COUNT(DISTINCT player_id) FROM sys_consume WHERE player_id = ? AND del_flag = '0'";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, playerId);
            return count != null && count > 0;
        }
    }
    
    private String getPlayerNameById(Long playerId) {
        try {
            String sql = "SELECT player_name FROM sys_player WHERE player_id = ? AND del_flag = '0'";
            return jdbcTemplate.queryForObject(sql, String.class, playerId);
        } catch (Exception e) {
            log.warn("获取玩家姓名失败，尝试从消费记录表查询: {}", e.getMessage());
            try {
                String sql = "SELECT player_name FROM sys_consume WHERE player_id = ? AND del_flag = '0' LIMIT 1";
                return jdbcTemplate.queryForObject(sql, String.class, playerId);
            } catch (Exception ex) {
                log.warn("从消费记录表获取玩家姓名也失败: {}", ex.getMessage());
                return null;
            }
        }
    }
    
    private Long findPlayerIdByName(String playerName) {
        try {
            String sql = "SELECT player_id FROM sys_player WHERE player_name LIKE ? AND del_flag = '0' LIMIT 1";
            return jdbcTemplate.queryForObject(sql, Long.class, "%" + playerName + "%");
        } catch (Exception e) {
            log.warn("根据姓名查找玩家失败，尝试从消费记录表查询: {}", e.getMessage());
            try {
                String sql = "SELECT player_id FROM sys_consume WHERE player_name LIKE ? AND del_flag = '0' LIMIT 1";
                return jdbcTemplate.queryForObject(sql, Long.class, "%" + playerName + "%");
            } catch (Exception ex) {
                log.warn("从消费记录表查找玩家也失败: {}", ex.getMessage());
                return null;
            }
        }
    }
    
    private String getPlayerNotFoundMessage(Long playerId, String playerName) {
        if (playerId != null) {
            return String.format("未找到编号为%d的玩家，请确认玩家编号是否正确。", playerId);
        } else {
            return String.format("未找到名为「%s」的玩家，请确认玩家姓名是否正确。", playerName);
        }
    }
    
    public String formatNaturalLanguage(ConsumptionResult result) {
        if (!result.success) {
            return result.message;
        }
        
        if (!result.playerExists) {
            return result.message;
        }
        
        String playerName = result.data.get("playerName") != null ? 
                result.data.get("playerName").toString() : "该玩家";
        Long playerId = result.data.get("playerId") != null ? 
                (Long) result.data.get("playerId") : null;
        Object totalConsumption = result.data.get("totalConsumption");
        Object consumptionCount = result.data.get("consumptionCount");
        Object lastTime = result.data.get("lastConsumptionTime");
        Boolean hasConsumption = (Boolean) result.data.get("hasConsumption");
        
        StringBuilder sb = new StringBuilder();
        
        if (playerId != null) {
            sb.append(playerId).append("号玩家");
        } else {
            sb.append(playerName);
        }
        
        if (hasConsumption != null && !hasConsumption) {
            sb.append("暂未产生消费记录。");
            return sb.toString();
        }
        
        sb.append("的累计消费金额为");
        
        if (totalConsumption != null && totalConsumption instanceof Number) {
            double amount = ((Number) totalConsumption).doubleValue();
            sb.append(String.format("%.2f", amount)).append("元");
        } else {
            sb.append("0元");
        }
        
        if (consumptionCount != null && ((Number) consumptionCount).intValue() > 0) {
            sb.append("，共消费").append(consumptionCount).append("次");
        }
        
        if (lastTime != null) {
            String timeStr = lastTime.toString();
            if (timeStr.contains("T")) {
                timeStr = timeStr.replace("T", " ").substring(0, Math.min(19, timeStr.length()));
            } else if (timeStr.length() > 19) {
                timeStr = timeStr.substring(0, 19);
            }
            sb.append("。最近一次消费时间：").append(timeStr);
        }
        
        return sb.toString();
    }
    
    private Long extractPlayerId(String text) {
        Pattern pattern = Pattern.compile("(\\d+)\\s*号|玩家\\s*(\\d+)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                if (matcher.group(i) != null) {
                    try {
                        return Long.parseLong(matcher.group(i));
                    } catch (NumberFormatException e) {
                        log.warn("无法解析玩家ID: {}", matcher.group(i));
                    }
                }
            }
        }
        return null;
    }
    
    private String extractPlayerName(String text) {
        Pattern pattern = Pattern.compile("玩家[：:]*\\s*([^\\s,，。！？]+)|姓名为\\s*([^\\s,，。！？]+)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                if (matcher.group(i) != null && matcher.group(i).length() > 1) {
                    return matcher.group(i);
                }
            }
        }
        return null;
    }
}
