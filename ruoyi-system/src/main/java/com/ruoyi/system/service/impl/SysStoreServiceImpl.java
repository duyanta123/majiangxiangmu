package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysStoreMapper;
import com.ruoyi.system.domain.SysStore;
import com.ruoyi.system.service.ISysStoreService;

@Service
public class SysStoreServiceImpl implements ISysStoreService {

    @Autowired
    private SysStoreMapper storeMapper;

    @Override
    public SysStore selectStoreById(Long storeId) {
        return storeMapper.selectStoreById(storeId);
    }

    @Override
    public List<SysStore> selectStoreList(SysStore store) {
        return storeMapper.selectStoreList(store);
    }

    @Override
    public int insertStore(SysStore store) {
        return storeMapper.insertStore(store);
    }

    @Override
    public int updateStore(SysStore store) {
        return storeMapper.updateStore(store);
    }

    @Override
    public int deleteStoreByIds(Long[] storeIds) {
        return storeMapper.deleteStoreByIds(storeIds);
    }

    @Override
    public boolean checkStoreCodeUnique(String storeCode) {
        SysStore store = storeMapper.checkStoreCodeUnique(storeCode);
        return store == null;
    }
}
