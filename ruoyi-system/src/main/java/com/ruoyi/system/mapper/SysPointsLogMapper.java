package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysPointsLog;

public interface SysPointsLogMapper {
    public SysPointsLog selectPointsLogById(Long logId);
    public List<SysPointsLog> selectPointsLogList(SysPointsLog log);
    public int insertPointsLog(SysPointsLog log);
    public int updatePointsLog(SysPointsLog log);
    public int deletePointsLogByIds(Long[] logIds);
}
