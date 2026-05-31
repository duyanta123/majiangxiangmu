package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysPointsLogMapper;
import com.ruoyi.system.domain.SysPointsLog;
import com.ruoyi.system.service.ISysPointsLogService;

@Service
public class SysPointsLogServiceImpl implements ISysPointsLogService {

    @Autowired
    private SysPointsLogMapper pointsLogMapper;

    @Override
    public SysPointsLog selectPointsLogById(Long logId) {
        return pointsLogMapper.selectPointsLogById(logId);
    }

    @Override
    public List<SysPointsLog> selectPointsLogList(SysPointsLog pointsLog) {
        return pointsLogMapper.selectPointsLogList(pointsLog);
    }

    @Override
    public int insertPointsLog(SysPointsLog pointsLog) {
        return pointsLogMapper.insertPointsLog(pointsLog);
    }

    @Override
    public int updatePointsLog(SysPointsLog pointsLog) {
        return pointsLogMapper.updatePointsLog(pointsLog);
    }

    @Override
    public int deletePointsLogByIds(Long[] logIds) {
        return pointsLogMapper.deletePointsLogByIds(logIds);
    }
}
