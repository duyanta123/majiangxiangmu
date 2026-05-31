package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysMemberLevelMapper;
import com.ruoyi.system.domain.SysMemberLevel;
import com.ruoyi.system.service.ISysMemberLevelService;

@Service
public class SysMemberLevelServiceImpl implements ISysMemberLevelService {

    @Autowired
    private SysMemberLevelMapper levelMapper;

    @Override
    public SysMemberLevel selectMemberLevelById(Long levelId) {
        return levelMapper.selectMemberLevelById(levelId);
    }

    @Override
    public List<SysMemberLevel> selectMemberLevelList(SysMemberLevel level) {
        return levelMapper.selectMemberLevelList(level);
    }

    @Override
    public int insertMemberLevel(SysMemberLevel level) {
        return levelMapper.insertMemberLevel(level);
    }

    @Override
    public int updateMemberLevel(SysMemberLevel level) {
        return levelMapper.updateMemberLevel(level);
    }

    @Override
    public int deleteMemberLevelByIds(Long[] levelIds) {
        return levelMapper.deleteMemberLevelByIds(levelIds);
    }

    @Override
    public boolean checkLevelCodeUnique(String levelCode) {
        SysMemberLevel level = levelMapper.checkLevelCodeUnique(levelCode);
        return level == null;
    }

    @Override
    public SysMemberLevel getLevelByPoints(Integer points) {
        return levelMapper.getLevelByPoints(points);
    }
}
