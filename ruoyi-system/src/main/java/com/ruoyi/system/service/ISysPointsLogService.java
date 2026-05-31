package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.SysPointsLog;

public interface ISysPointsLogService {
    public SysPointsLog selectPointsLogById(Long logId);
    public List<SysPointsLog> selectPointsLogList(SysPointsLog pointsLog);
    public int insertPointsLog(SysPointsLog pointsLog);
    public int updatePointsLog(SysPointsLog pointsLog);
    public int deletePointsLogByIds(Long[] logIds);
}
