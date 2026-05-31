package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.SysConsume;

public interface ISysConsumeService {
    public SysConsume selectConsumeById(Long consumeId);
    public List<SysConsume> selectConsumeList(SysConsume consume);
    public int insertConsume(SysConsume consume);
    public int updateConsume(SysConsume consume);
    public int deleteConsumeByIds(Long[] consumeIds);
    public String generateConsumeNo();
}
