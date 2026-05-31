package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysMemberLevel;
import org.apache.ibatis.annotations.Param;

public interface SysMemberLevelMapper {
    public SysMemberLevel selectMemberLevelById(Long levelId);
    public List<SysMemberLevel> selectMemberLevelList(SysMemberLevel level);
    public int insertMemberLevel(SysMemberLevel level);
    public int updateMemberLevel(SysMemberLevel level);
    public int deleteMemberLevelByIds(Long[] levelIds);
    public SysMemberLevel checkLevelCodeUnique(@Param("levelCode") String levelCode);
    public SysMemberLevel getLevelByPoints(@Param("points") Integer points);
}
