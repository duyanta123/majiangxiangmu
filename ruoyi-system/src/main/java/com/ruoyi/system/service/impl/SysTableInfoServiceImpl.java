package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysTableInfoMapper;
import com.ruoyi.system.domain.SysTableInfo;
import com.ruoyi.system.service.ISysTableInfoService;

/**
 * 桌台信息Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class SysTableInfoServiceImpl implements ISysTableInfoService {

    @Autowired
    private SysTableInfoMapper tableInfoMapper;

    /**
     * 查询桌台信息
     *
     * @param tableId 桌台ID
     * @return 桌台信息
     */
    @Override
    public SysTableInfo selectTableInfoById(Long tableId) {
        return tableInfoMapper.selectTableInfoById(tableId);
    }

    /**
     * 查询桌台信息列表
     *
     * @param tableInfo 桌台信息
     * @return 桌台信息
     */
    @Override
    public List<SysTableInfo> selectTableInfoList(SysTableInfo tableInfo) {
        return tableInfoMapper.selectTableInfoList(tableInfo);
    }

    /**
     * 新增桌台信息
     *
     * @param tableInfo 桌台信息
     * @return 结果
     */
    @Override
    public int insertTableInfo(SysTableInfo tableInfo) {
        return tableInfoMapper.insertTableInfo(tableInfo);
    }

    /**
     * 修改桌台信息
     *
     * @param tableInfo 桌台信息
     * @return 结果
     */
    @Override
    public int updateTableInfo(SysTableInfo tableInfo) {
        return tableInfoMapper.updateTableInfo(tableInfo);
    }

    /**
     * 删除桌台信息
     *
     * @param tableIds 需要删除的桌台ID
     * @return 结果
     */
    @Override
    public int deleteTableInfoByIds(Long[] tableIds) {
        return tableInfoMapper.deleteTableInfoByIds(tableIds);
    }

    /**
     * 校验桌台编号唯一性
     */
    @Override
    public boolean checkTableCodeUnique(String tableCode) {
        SysTableInfo info = tableInfoMapper.checkTableCodeUnique(tableCode);
        return info == null;
    }

    /**
     * 更新桌台状态
     */
    @Override
    public int updateTableStatus(Long tableId, String tableStatus) {
        return tableInfoMapper.updateTableStatus(tableId, tableStatus);
    }
}
