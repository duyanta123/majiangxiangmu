package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.SysStore;

public interface ISysStoreService {
    public SysStore selectStoreById(Long storeId);
    public List<SysStore> selectStoreList(SysStore store);
    public int insertStore(SysStore store);
    public int updateStore(SysStore store);
    public int deleteStoreByIds(Long[] storeIds);
    public boolean checkStoreCodeUnique(String storeCode);
}
