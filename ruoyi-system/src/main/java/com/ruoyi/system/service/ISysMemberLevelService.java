package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.SysMemberLevel;

public interface ISysMemberLevelService {
    public SysMemberLevel selectMemberLevelById(Long levelId);
    public List<SysMemberLevel> selectMemberLevelList(SysMemberLevel level);
    public int insertMemberLevel(SysMemberLevel level);
    public int updateMemberLevel(SysMemberLevel level);
    public int deleteMemberLevelByIds(Long[] levelIds);
    public boolean checkLevelCodeUnique(String levelCode);
    public SysMemberLevel getLevelByPoints(Integer points);
}
