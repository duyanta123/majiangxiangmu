package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysStore;
import org.apache.ibatis.annotations.Param;

public interface SysStoreMapper {
    public SysStore selectStoreById(Long storeId);
    public List<SysStore> selectStoreList(SysStore store);
    public int insertStore(SysStore store);
    public int updateStore(SysStore store);
    public int deleteStoreByIds(Long[] storeIds);
    public SysStore checkStoreCodeUnique(@Param("storeCode") String storeCode);
}
