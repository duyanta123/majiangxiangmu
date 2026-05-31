package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysConsume;
import org.apache.ibatis.annotations.Param;

public interface SysConsumeMapper {
    public SysConsume selectConsumeById(Long consumeId);
    public List<SysConsume> selectConsumeList(SysConsume consume);
    public int insertConsume(SysConsume consume);
    public int updateConsume(SysConsume consume);
    public int deleteConsumeByIds(Long[] consumeIds);
    public SysConsume selectConsumeByVerificationCode(String verificationCode);
}
