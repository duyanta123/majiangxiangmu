package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.SysMember;

public interface ISysMemberService {
    public SysMember selectMemberById(Long memberId);
    public List<SysMember> selectMemberList(SysMember member);
    public int insertMember(SysMember member);
    public int updateMember(SysMember member);
    public int deleteMemberByIds(Long[] memberIds);
    public int addPoints(Long memberId, Integer points, String sourceType, Long sourceId, String description);
    public int deductPoints(Long memberId, Integer points, String sourceType, Long sourceId, String description);
}
