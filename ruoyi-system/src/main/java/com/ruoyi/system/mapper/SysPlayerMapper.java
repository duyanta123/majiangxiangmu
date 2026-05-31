package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysPlayer;
import org.apache.ibatis.annotations.Param;

public interface SysPlayerMapper {
    public SysPlayer selectPlayerById(Long playerId);
    public List<SysPlayer> selectPlayerList(SysPlayer player);
    public int insertPlayer(SysPlayer player);
    public int updatePlayer(SysPlayer player);
    public int deletePlayerByIds(Long[] playerIds);
    public SysPlayer checkPhoneUnique(@Param("playerPhone") String playerPhone);
}
