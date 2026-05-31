package com.ruoyi.system.mapper;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.dto.AnalysisDTO;
import com.ruoyi.system.dto.RevenueStats;
import com.ruoyi.system.dto.MemberStats;

public interface SysAnalysisMapper {
    Map<String, Object> getTodayStats();
    Map<String, Object> getWeekStats();
    Map<String, Object> getMonthStats();
    Map<String, Object> getStoreOverview();
    List<RevenueStats> getRevenueTrend(AnalysisDTO dto);
    List<MemberStats> getMemberTrend(AnalysisDTO dto);
    Map<String, Object> getStoreBasicStats(Long storeId);
    Map<String, Object> getStoreRevenueStats(Long storeId);
    Map<String, Object> getStoreMemberStats(Long storeId);
    List<Map<String, Object>> getConsumptionRanking(Integer limit);
    List<Map<String, Object>> getPointsRanking(Integer limit);
    List<Map<String, Object>> getTableTypeRevenue(Long storeId);
    List<Map<String, Object>> getTableUtilization(Long storeId);
}
