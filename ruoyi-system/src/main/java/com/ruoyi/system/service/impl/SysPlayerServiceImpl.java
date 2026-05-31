package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysPlayerMapper;
import com.ruoyi.system.domain.SysPlayer;
import com.ruoyi.system.service.ISysPlayerService;

@Service
public class SysPlayerServiceImpl implements ISysPlayerService {

    @Autowired
    private SysPlayerMapper playerMapper;

    @Override
    public SysPlayer selectPlayerById(Long playerId) {
        return playerMapper.selectPlayerById(playerId);
    }

    @Override
    public List<SysPlayer> selectPlayerList(SysPlayer player) {
        return playerMapper.selectPlayerList(player);
    }

    @Override
    public int insertPlayer(SysPlayer player) {
        return playerMapper.insertPlayer(player);
    }

    @Override
    public int updatePlayer(SysPlayer player) {
        return playerMapper.updatePlayer(player);
    }

    @Override
    public int deletePlayerByIds(Long[] playerIds) {
        return playerMapper.deletePlayerByIds(playerIds);
    }

    @Override
    public boolean checkPhoneUnique(String playerPhone) {
        SysPlayer player = playerMapper.checkPhoneUnique(playerPhone);
        return player == null;
    }
}
