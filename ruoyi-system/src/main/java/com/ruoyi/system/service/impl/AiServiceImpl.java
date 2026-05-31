package com.ruoyi.system.service.impl;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysAiMapper;
import com.ruoyi.system.mapper.SysMemberMapper;
import com.ruoyi.system.mapper.SysPlayerMapper;
import com.ruoyi.system.mapper.SysConsumeMapper;
import com.ruoyi.system.mapper.SysStoreMapper;
import com.ruoyi.system.domain.SysAiSchedule;
import com.ruoyi.system.domain.SysAiRecommendation;
import com.ruoyi.system.domain.SysAiChat;
import com.ruoyi.system.domain.SysPlayer;
import com.ruoyi.system.domain.SysMember;
import com.ruoyi.system.domain.SysStore;
import com.ruoyi.system.dto.AiRequestDTO;
import com.ruoyi.system.dto.AiChatDTO;
import com.ruoyi.system.service.IAiService;
import com.ruoyi.system.service.DeepSeekService;

@Service
public class AiServiceImpl implements IAiService {

    @Autowired
    private SysAiMapper aiMapper;

    @Autowired
    private SysMemberMapper memberMapper;

    @Autowired
    private SysPlayerMapper playerMapper;

    @Autowired
    private SysConsumeMapper consumeMapper;

    @Autowired
    private SysStoreMapper storeMapper;

    @Autowired
    private DeepSeekService deepSeekService;

    @Override
    public int addSchedule(SysAiSchedule schedule) {
        return aiMapper.insertScheduleBatch(List.of(schedule));
    }

    @Override
    public List<SysAiSchedule> getScheduleList(Long storeId, String date) {
        return aiMapper.getScheduleList(storeId, date);
    }

    @Override
    public List<SysAiSchedule> getScheduleHistory(Long storeId) {
        return aiMapper.getScheduleHistory(storeId);
    }

    @Override
    public int updateSchedule(SysAiSchedule schedule) {
        return aiMapper.updateSchedule(schedule);
    }

    @Override
    public int deleteSchedule(Long scheduleId) {
        return aiMapper.deleteSchedule(scheduleId);
    }

    @Override
    public int addRecommendations(List<SysAiRecommendation> recommendations) {
        return aiMapper.insertRecommendationBatch(recommendations);
    }

    @Override
    public List<SysAiRecommendation> getRecommendations(Long playerId) {
        return aiMapper.getRecommendations(playerId);
    }

    @Override
    public int chat(SysAiChat chat) {
        return aiMapper.insertChat(chat);
    }

    @Override
    public List<SysAiChat> getChatHistory(Long playerId) {
        return aiMapper.getChatHistory(playerId);
    }

    @Override
    public Map<String, Object> getStoreAnalysis(Long storeId) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> data = aiMapper.getStoreAnalysis(storeId);
        result.put("data", data);
        result.put("suggestions", generateStoreSuggestions(data));
        return result;
    }

    @Override
    public Map<String, Object> generateSchedule(AiRequestDTO request) {
        Map<String, Object> result = new HashMap<>();
        Long storeId = request.getStoreId();
        String date = request.getInput();

        SysStore store = storeMapper.selectStoreById(storeId);
        String storeName = store != null ? store.getStoreName() : "未知门店";

        Map<String, Object> storeData = aiMapper.getStoreAnalysis(storeId);
        List<SysAiSchedule> schedules = generateAISchedules(storeId, storeName, date, storeData);
        if (!schedules.isEmpty()) {
            aiMapper.insertScheduleBatch(schedules);
        }
        result.put("data", schedules);
        result.put("message", buildScheduleResponse(schedules, storeData));
        result.put("storeName", storeName);
        result.put("success", true);
        return result;
    }

    @Override
    public Map<String, Object> generateRecommendations(AiRequestDTO request) {
        Map<String, Object> result = new HashMap<>();
        Long playerId = request.getPlayerId();

        SysPlayer player = playerMapper.selectPlayerById(playerId);
        if (player == null) {
            result.put("success", false);
            result.put("message", "该玩家ID不存在，请重新输入");
            return result;
        }

        List<SysAiRecommendation> recommendations = generateAIRecommendations(playerId);
        if (!recommendations.isEmpty()) {
            aiMapper.insertRecommendationBatch(recommendations);
        }
        result.put("success", true);
        result.put("data", recommendations);
        result.put("message", buildRecommendationResponse(recommendations));
        return result;
    }

    public AiChatDTO chatWithMemory(AiChatDTO chatDTO) {
        String userInput = chatDTO.getUserInput();

        Map<String, Object> playerInfo = new HashMap<>();

        Long playerId = chatDTO.getPlayerId();
        if (playerId != null && playerId > 0) {
            SysPlayer player = playerMapper.selectPlayerById(playerId);
            if (player != null) {
                if (chatDTO.getPlayerName() == null || chatDTO.getPlayerName().isEmpty()) {
                    chatDTO.setPlayerName(player.getPlayerName());
                }
                if (chatDTO.getPlayerPhone() == null || chatDTO.getPlayerPhone().isEmpty()) {
                    chatDTO.setPlayerPhone(player.getPlayerPhone());
                }
                playerInfo.put("playerName", player.getPlayerName());
                playerInfo.put("playerPhone", player.getPlayerPhone());
                if (player.getTotalConsumption() != null) {
                    playerInfo.put("totalConsumption", player.getTotalConsumption());
                }
            }

            SysMember member = memberMapper.selectMemberByPlayerId(playerId);
            if (member != null) {
                String memberLevel = member.getLevelName();
                playerInfo.put("memberLevel", memberLevel != null ? memberLevel : "普通玩家");
                playerInfo.put("points", member.getAvailablePoints() != null ? member.getAvailablePoints() : 0);
                if (member.getTotalConsumption() != null) {
                    playerInfo.put("totalConsumption", member.getTotalConsumption());
                }
                playerInfo.put("consumeCount", member.getConsumeCount() != null ? member.getConsumeCount() : 0);
            }

            if (chatDTO.getMemberLevel() != null && !chatDTO.getMemberLevel().isEmpty()) {
                playerInfo.put("memberLevel", chatDTO.getMemberLevel());
            }
            if (chatDTO.getTotalConsumption() != null) {
                playerInfo.put("totalConsumption", chatDTO.getTotalConsumption());
            }
            if (chatDTO.getPoints() != null) {
                playerInfo.put("points", chatDTO.getPoints());
            }
            if (chatDTO.getConsumeCount() != null) {
                playerInfo.put("consumeCount", chatDTO.getConsumeCount());
            }
        }

        List<SysAiChat> history = aiMapper.getChatHistory(null);
        Map<String, String> contextMap = new LinkedHashMap<>();
        if (history != null) {
            for (int i = 0; i < history.size() && i < 10; i++) {
                SysAiChat chatItem = history.get(i);
                if (chatItem != null) {
                    String historyUserInput = chatItem.getUserInput();
                    String historyAiResponse = chatItem.getAiResponse();
                    if (historyUserInput != null && !historyUserInput.isEmpty()) {
                        contextMap.put("user_" + i, "user::" + historyUserInput);
                    }
                    if (historyAiResponse != null && !historyAiResponse.isEmpty()) {
                        contextMap.put("ai_" + i, "assistant::" + historyAiResponse);
                    }
                }
            }
        }

        String aiResponse = deepSeekService.chat(userInput, playerInfo, contextMap);

        SysAiChat chat = new SysAiChat();
        chat.setPlayerId(playerId);
        chat.setPlayerName(chatDTO.getPlayerName());
        chat.setPlayerPhone(chatDTO.getPlayerPhone());
        chat.setUserInput(userInput);
        chat.setAiResponse(aiResponse);
        chat.setIntent(chatDTO.getIntent());
        chat.setChatType(chatDTO.getChatType());
        aiMapper.insertChat(chat);

        chatDTO.setAiResponse(aiResponse);
        return chatDTO;
    }

    private List<SysAiSchedule> generateAISchedules(Long storeId, String storeName, String date, Map<String, Object> storeData) {
        List<SysAiSchedule> schedules = new ArrayList<>();

        String[] timeSlots = {"09:00-12:00", "12:00-15:00", "15:00-18:00", "18:00-21:00", "21:00-24:00"};
        String[] positions = {"前台接待", "服务员", "收银员", "保洁员", "值班经理"};
        String[] employees = {"张三", "李四", "王五", "赵六", "钱七", "孙八", "周九", "吴十"};

        int activePlayers = 0;
        if (storeData != null && storeData.get("activePlayers") != null) {
            activePlayers = ((Number) storeData.get("activePlayers")).intValue();
        }

        java.time.LocalDate localDate = java.time.LocalDate.parse(date);
        boolean isWeekend = localDate.getDayOfWeek() == java.time.DayOfWeek.SATURDAY ||
                           localDate.getDayOfWeek() == java.time.DayOfWeek.SUNDAY;

        int employeeIndex = 0;
        for (String timeSlot : timeSlots) {
            int staffCount = getStaffCountForSlot(timeSlot, activePlayers, isWeekend);

            for (int i = 0; i < staffCount && employeeIndex < employees.length; i++) {
                SysAiSchedule schedule = new SysAiSchedule();
                schedule.setStoreId(storeId);
                schedule.setStoreName(storeName);
                schedule.setDate(date);
                schedule.setTimeSlot(timeSlot);
                schedule.setEmployeeName(employees[employeeIndex % employees.length]);
                schedule.setPosition(positions[employeeIndex % positions.length]);
                schedule.setWorkload(calculateWorkload(timeSlot, activePlayers, isWeekend));
                schedule.setStatus("0");
                schedule.setAiSuggestion(getScheduleSuggestion(timeSlot, activePlayers, isWeekend));
                schedules.add(schedule);
                employeeIndex++;
            }
        }
        return schedules;
    }

    private int getStaffCountForSlot(String timeSlot, int activePlayers, boolean isWeekend) {
        int baseCount = 2;

        if (timeSlot.contains("18:00") || timeSlot.contains("21:00")) {
            baseCount = 4;
            if (activePlayers > 30 || isWeekend) {
                baseCount = 5;
            }
        } else if (timeSlot.contains("12:00")) {
            baseCount = 3;
            if (activePlayers > 20) {
                baseCount = 4;
            }
        } else {
            baseCount = 2;
            if (activePlayers > 15) {
                baseCount = 3;
            }
        }

        return baseCount;
    }

    private int calculateWorkload(String timeSlot, int activePlayers, boolean isWeekend) {
        int baseWorkload = 3;

        if (timeSlot.contains("18:00") || timeSlot.contains("21:00")) {
            baseWorkload = 8;
            if (activePlayers > 30 || isWeekend) {
                baseWorkload = 10;
            }
        } else if (timeSlot.contains("12:00")) {
            baseWorkload = 6;
            if (activePlayers > 20) {
                baseWorkload = 8;
            }
        } else {
            baseWorkload = 3 + (int) (Math.random() * 3);
        }

        return baseWorkload;
    }

    private String getScheduleSuggestion(String timeSlot, int activePlayers, boolean isWeekend) {
        if (timeSlot.contains("18:00")) {
            if (activePlayers > 30 || isWeekend) {
                return "周五/周末晚间客流高峰，建议增加2名前台员工，注意翻台节奏";
            }
            return "晚间营业高峰，确保服务质量，及时清理桌台";
        } else if (timeSlot.contains("21:00")) {
            return "深夜时段，注意员工排班轮换，保持服务热情";
        } else if (timeSlot.contains("12:00")) {
            return "午间用餐高峰，确保快速响应客户需求";
        } else if (timeSlot.contains("15:00")) {
            if (activePlayers < 10) {
                return "当前客流较少，可安排员工轮休或培训";
            }
            return "下午时段，保持标准服务配置";
        } else {
            return "上午时段，做好开业准备，检查设施设备";
        }
    }

    private List<SysAiRecommendation> generateAIRecommendations(Long playerId) {
        List<SysAiRecommendation> recommendations = new ArrayList<>();

        SysPlayer player = playerMapper.selectPlayerById(playerId);
        if (player == null) return recommendations;

        SysMember member = memberMapper.selectMemberByPlayerId(playerId);

        SysAiRecommendation rec1 = new SysAiRecommendation();
        rec1.setPlayerId(playerId);
        rec1.setPlayerName(player.getPlayerName());
        rec1.setPlayerPhone(player.getPlayerPhone());
        rec1.setRecType("优惠活动");
        rec1.setRecContent("本周到店消费满200元减50元");
        rec1.setRecReason("根据您的消费记录，您是我们的活跃客户，本周我们特别为您准备了专属优惠");
        rec1.setStatus("0");
        rec1.setExpireTime(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L));
        recommendations.add(rec1);

        if (member != null) {
            SysAiRecommendation rec2 = new SysAiRecommendation();
            rec2.setPlayerId(playerId);
            rec2.setPlayerName(player.getPlayerName());
            rec2.setPlayerPhone(player.getPlayerPhone());
            rec2.setRecType("会员升级");
            rec2.setRecContent("再消费" + (5000 - member.getTotalConsumption().doubleValue()) + "元即可升级为黄金会员");
            rec2.setRecReason("您目前的消费已达" + member.getTotalConsumption() + "元，距离升级仅差" + (5000 - member.getTotalConsumption().doubleValue()) + "元");
            rec2.setStatus("0");
            rec2.setExpireTime(new Date(System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000L));
            recommendations.add(rec2);

            if (member.getAvailablePoints() != null && member.getAvailablePoints() > 100) {
                SysAiRecommendation rec3 = new SysAiRecommendation();
                rec3.setPlayerId(playerId);
                rec3.setPlayerName(player.getPlayerName());
                rec3.setPlayerPhone(player.getPlayerPhone());
                rec3.setRecType("积分兑换");
                rec3.setRecContent("您有" + member.getAvailablePoints() + "积分，可兑换价值" + (member.getAvailablePoints() / 100) + "元的商品");
                rec3.setRecReason("积分有效期至年底，建议及时兑换");
                rec3.setStatus("0");
                rec3.setExpireTime(new Date(System.currentTimeMillis() + 60 * 24 * 60 * 60 * 1000L));
                recommendations.add(rec3);
            }
        }

        SysAiRecommendation rec4 = new SysAiRecommendation();
        rec4.setPlayerId(playerId);
        rec4.setPlayerName(player.getPlayerName());
        rec4.setPlayerPhone(player.getPlayerPhone());
        rec4.setRecType("活动邀请");
        rec4.setRecContent("邀请1名新会员可获得100元优惠券");
        rec4.setRecReason("老带新活动正在进行中，邀请好友双方都有优惠");
        rec4.setStatus("0");
        rec4.setExpireTime(new Date(System.currentTimeMillis() + 14 * 24 * 60 * 60 * 1000L));
        recommendations.add(rec4);

        return recommendations;
    }

    private String buildScheduleResponse(List<SysAiSchedule> schedules, Map<String, Object> storeData) {
        StringBuilder sb = new StringBuilder();
        sb.append("根据门店数据分析和建议：\n\n");

        if (storeData != null) {
            Object activePlayers = storeData.get("activePlayers");
            if (activePlayers != null) {
                sb.append("- 当前活跃玩家：").append(activePlayers).append("人\n");
            }
            Object completedOrders = storeData.get("completedOrders");
            if (completedOrders != null) {
                sb.append("- 今日完成订单：").append(completedOrders).append("单\n");
            }
        }

        sb.append("\n排班建议：\n");
        for (SysAiSchedule s : schedules) {
            sb.append("- ").append(s.getTimeSlot()).append("：").append(s.getEmployeeName())
              .append("(").append(s.getPosition()).append(") - ").append(s.getAiSuggestion()).append("\n");
        }
        return sb.toString();
    }

    private String buildRecommendationResponse(List<SysAiRecommendation> recommendations) {
        StringBuilder sb = new StringBuilder();
        sb.append("为该玩家生成了").append(recommendations.size()).append("条个性化推荐：\n\n");
        for (SysAiRecommendation r : recommendations) {
            sb.append("- ").append(r.getRecType()).append("：").append(r.getRecContent()).append("\n");
            sb.append("  理由：").append(r.getRecReason()).append("\n\n");
        }
        return sb.toString();
    }

    private List<String> generateStoreSuggestions(Map<String, Object> data) {
        List<String> suggestions = new ArrayList<>();

        if (data != null) {
            Object activePlayers = data.get("activePlayers");
            if (activePlayers != null) {
                int players = ((Number) activePlayers).intValue();
                if (players < 10) {
                    suggestions.add("近期活跃玩家较少，建议加强会员营销推广");
                } else if (players > 50) {
                    suggestions.add("客流较高，建议增加服务人手");
                }
            }

            Object totalRevenue = data.get("totalRevenue");
            if (totalRevenue != null) {
                double revenue = ((Number) totalRevenue).doubleValue();
                if (revenue > 10000) {
                    suggestions.add("今日收入表现优秀，可考虑推出新品吸引更多客户");
                }
            }
        }

        if (suggestions.isEmpty()) {
            suggestions.add("优化员工排班以匹配客流高峰");
            suggestions.add("增加热门时段的预订名额");
            suggestions.add("针对高消费会员提供专属优惠");
        }

        return suggestions;
    }
}
