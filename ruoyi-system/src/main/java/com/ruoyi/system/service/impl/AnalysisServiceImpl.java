package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysAnalysisMapper;
import com.ruoyi.system.dto.AnalysisDTO;
import com.ruoyi.system.dto.RevenueStats;
import com.ruoyi.system.dto.MemberStats;
import com.ruoyi.system.service.AnalysisService;

@Service
public class AnalysisServiceImpl implements AnalysisService {

    @Autowired
    private SysAnalysisMapper analysisMapper;

    @Override
    public Map<String, Object> getDashboard() {
        Map<String, Object> result = new HashMap<>();
        result.put("todayStats", getTodayStats());
        result.put("weekStats", getWeekStats());
        result.put("monthStats", getMonthStats());
        result.put("storeOverview", analysisMapper.getStoreOverview());
        return result;
    }

    @Override
    public Map<String, Object> getTodayStats() {
        return analysisMapper.getTodayStats();
    }

    @Override
    public Map<String, Object> getWeekStats() {
        return analysisMapper.getWeekStats();
    }

    @Override
    public Map<String, Object> getMonthStats() {
        return analysisMapper.getMonthStats();
    }

    @Override
    public List<RevenueStats> getRevenueTrend(AnalysisDTO dto) {
        return analysisMapper.getRevenueTrend(dto);
    }

    @Override
    public List<MemberStats> getMemberTrend(AnalysisDTO dto) {
        return analysisMapper.getMemberTrend(dto);
    }

    @Override
    public Map<String, Object> getStoreAnalysis(Long storeId) {
        Map<String, Object> result = new HashMap<>();
        result.put("basic", analysisMapper.getStoreBasicStats(storeId));
        result.put("revenue", analysisMapper.getStoreRevenueStats(storeId));
        result.put("member", analysisMapper.getStoreMemberStats(storeId));
        return result;
    }

    @Override
    public Map<String, Object> getConsumeRanking(Integer limit) {
        Map<String, Object> result = new HashMap<>();
        result.put("consumptionRanking", analysisMapper.getConsumptionRanking(limit));
        return result;
    }

    @Override
    public List<Map<String, Object>> getPointsRanking(Integer limit) {
        return analysisMapper.getPointsRanking(limit);
    }

    @Override
    public List<Map<String, Object>> getTableTypeRevenue(Long storeId) {
        return analysisMapper.getTableTypeRevenue(storeId);
    }

    @Override
    public List<Map<String, Object>> getTableUtilization(Long storeId) {
        return analysisMapper.getTableUtilization(storeId);
    }
}
