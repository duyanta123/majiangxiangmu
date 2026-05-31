package com.ruoyi.system.mapper;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.SysAiSchedule;
import com.ruoyi.system.domain.SysAiRecommendation;
import com.ruoyi.system.domain.SysAiChat;

public interface SysAiMapper {
    int insertScheduleBatch(List<SysAiSchedule> schedules);
    List<SysAiSchedule> getScheduleList(Long storeId, String date);
    List<SysAiSchedule> getScheduleHistory(Long storeId);
    int updateSchedule(SysAiSchedule schedule);
    int deleteSchedule(Long scheduleId);
    int insertRecommendationBatch(List<SysAiRecommendation> recommendations);
    List<SysAiRecommendation> getRecommendations(Long playerId);
    int insertChat(SysAiChat chat);
    List<SysAiChat> getChatHistory(Long playerId);
    Map<String, Object> getStoreAnalysis(Long storeId);
}
