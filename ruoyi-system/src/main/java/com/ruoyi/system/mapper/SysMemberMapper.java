package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysMember;

public interface SysMemberMapper {
    public SysMember selectMemberById(Long memberId);
    public SysMember selectMemberByPlayerId(Long playerId);
    public List<SysMember> selectMemberList(SysMember member);
    public int insertMember(SysMember member);
    public int updateMember(SysMember member);
    public int deleteMemberByIds(Long[] memberIds);
}
