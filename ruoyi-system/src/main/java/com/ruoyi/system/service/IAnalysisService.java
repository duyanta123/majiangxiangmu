package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.dto.AnalysisDTO;
import com.ruoyi.system.dto.RevenueStats;
import com.ruoyi.system.dto.MemberStats;

public interface IAnalysisService {
    public Map<String, Object> getDashboard();
    public Map<String, Object> getTodayStats();
    public Map<String, Object> getWeekStats();
    public Map<String, Object> getMonthStats();
    public List<RevenueStats> getRevenueTrend(AnalysisDTO dto);
    public List<MemberStats> getMemberTrend(AnalysisDTO dto);
    public Map<String, Object> getStoreAnalysis(Long storeId);
    public Map<String, Object> getConsumeRanking(Integer limit);
    public List<Map<String, Object>> getPointsRanking(Integer limit);
    public List<Map<String, Object>> getTableTypeRevenue(Long storeId);
    public List<Map<String, Object>> getTableUtilization(Long storeId);
}
