package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.SysPlayer;

public interface ISysPlayerService {
    public SysPlayer selectPlayerById(Long playerId);
    public List<SysPlayer> selectPlayerList(SysPlayer player);
    public int insertPlayer(SysPlayer player);
    public int updatePlayer(SysPlayer player);
    public int deletePlayerByIds(Long[] playerIds);
    public boolean checkPhoneUnique(String playerPhone);
}
