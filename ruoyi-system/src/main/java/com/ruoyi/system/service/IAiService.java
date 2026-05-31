package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.SysAiSchedule;
import com.ruoyi.system.domain.SysAiRecommendation;
import com.ruoyi.system.domain.SysAiChat;
import com.ruoyi.system.dto.AiRequestDTO;
import com.ruoyi.system.dto.AiChatDTO;

public interface IAiService {
    public int addSchedule(SysAiSchedule schedule);
    public List<SysAiSchedule> getScheduleList(Long storeId, String date);
    public List<SysAiSchedule> getScheduleHistory(Long storeId);
    public int updateSchedule(SysAiSchedule schedule);
    public int deleteSchedule(Long scheduleId);
    public int addRecommendations(List<SysAiRecommendation> recommendations);
    public List<SysAiRecommendation> getRecommendations(Long playerId);
    public int chat(SysAiChat chat);
    public List<SysAiChat> getChatHistory(Long playerId);
    public Map<String, Object> getStoreAnalysis(Long storeId);
    public Map<String, Object> generateSchedule(AiRequestDTO request);
    public Map<String, Object> generateRecommendations(AiRequestDTO request);
    public AiChatDTO chatWithMemory(AiChatDTO chatDTO);
}
