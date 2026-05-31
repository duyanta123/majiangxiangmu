package com.ruoyi.system.service.impl;

import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.mapper.SysMemberMapper;
import com.ruoyi.system.mapper.SysPointsLogMapper;
import com.ruoyi.system.domain.SysMember;
import com.ruoyi.system.domain.SysPointsLog;
import com.ruoyi.system.domain.SysMemberLevel;
import com.ruoyi.system.service.ISysMemberService;
import com.ruoyi.system.service.ISysMemberLevelService;

@Service
public class SysMemberServiceImpl implements ISysMemberService {

    @Autowired
    private SysMemberMapper memberMapper;

    @Autowired
    private SysPointsLogMapper pointsLogMapper;

    @Autowired
    private ISysMemberLevelService levelService;

    @Override
    public SysMember selectMemberById(Long memberId) {
        return memberMapper.selectMemberById(memberId);
    }

    @Override
    public List<SysMember> selectMemberList(SysMember member) {
        return memberMapper.selectMemberList(member);
    }

    @Override
    public int insertMember(SysMember member) {
        if (member.getCardNo() == null) {
            member.setCardNo("VIP" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        }
        if (member.getMemberSince() == null) {
            member.setMemberSince(new Date());
        }
        return memberMapper.insertMember(member);
    }

    @Override
    public int updateMember(SysMember member) {
        return memberMapper.updateMember(member);
    }

    @Override
    public int deleteMemberByIds(Long[] memberIds) {
        return memberMapper.deleteMemberByIds(memberIds);
    }

    @Override
    @Transactional
    public int addPoints(Long memberId, Integer points, String sourceType, Long sourceId, String description) {
        SysMember member = memberMapper.selectMemberById(memberId);
        if (member == null) return 0;
        
        int before = member.getAvailablePoints() != null ? member.getAvailablePoints() : 0;
        int after = before + points;
        
        SysPointsLog log = new SysPointsLog();
        log.setMemberId(memberId);
        log.setPlayerName(member.getPlayerName());
        log.setPlayerPhone(member.getPlayerPhone());
        log.setChangeType("0");
        log.setPoints(points);
        log.setBalanceBefore(before);
        log.setBalanceAfter(after);
        log.setSourceType(sourceType);
        log.setSourceId(sourceId);
        log.setDescription(description);
        pointsLogMapper.insertPointsLog(log);
        
        member.setAvailablePoints(after);
        member.setTotalPoints((member.getTotalPoints() != null ? member.getTotalPoints() : 0) + points);
        
        SysMemberLevel level = levelService.getLevelByPoints(member.getTotalPoints());
        if (level != null && !level.getLevelId().equals(member.getLevelId())) {
            member.setLevelId(level.getLevelId());
            member.setLevelName(level.getLevelName());
        }
        
        return memberMapper.updateMember(member);
    }

    @Override
    @Transactional
    public int deductPoints(Long memberId, Integer points, String sourceType, Long sourceId, String description) {
        SysMember member = memberMapper.selectMemberById(memberId);
        if (member == null) return 0;
        
        int before = member.getAvailablePoints() != null ? member.getAvailablePoints() : 0;
        if (before < points) return -1;
        
        int after = before - points;
        
        SysPointsLog log = new SysPointsLog();
        log.setMemberId(memberId);
        log.setPlayerName(member.getPlayerName());
        log.setPlayerPhone(member.getPlayerPhone());
        log.setChangeType("1");
        log.setPoints(points);
        log.setBalanceBefore(before);
        log.setBalanceAfter(after);
        log.setSourceType(sourceType);
        log.setSourceId(sourceId);
        log.setDescription(description);
        pointsLogMapper.insertPointsLog(log);
        
        member.setAvailablePoints(after);
        return memberMapper.updateMember(member);
    }
}
