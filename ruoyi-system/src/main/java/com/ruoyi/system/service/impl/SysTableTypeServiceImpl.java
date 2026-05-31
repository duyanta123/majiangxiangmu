package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.mapper.SysTableTypeMapper;
import com.ruoyi.system.domain.SysTableType;
import com.ruoyi.system.service.ISysTableTypeService;

/**
 * 桌台类型Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class SysTableTypeServiceImpl implements ISysTableTypeService {

    @Autowired
    private SysTableTypeMapper tableTypeMapper;

    /**
     * 查询桌台类型
     *
     * @param typeId 桌台类型ID
     * @return 桌台类型
     */
    @Override
    public SysTableType selectTableTypeById(Long typeId) {
        return tableTypeMapper.selectTableTypeById(typeId);
    }

    /**
     * 查询桌台类型列表
     *
     * @param tableType 桌台类型
     * @return 桌台类型
     */
    @Override
    public List<SysTableType> selectTableTypeList(SysTableType tableType) {
        return tableTypeMapper.selectTableTypeList(tableType);
    }

    /**
     * 新增桌台类型
     *
     * @param tableType 桌台类型
     * @return 结果
     */
    @Override
    public int insertTableType(SysTableType tableType) {
        return tableTypeMapper.insertTableType(tableType);
    }

    /**
     * 修改桌台类型
     *
     * @param tableType 桌台类型
     * @return 结果
     */
    @Override
    public int updateTableType(SysTableType tableType) {
        return tableTypeMapper.updateTableType(tableType);
    }

    /**
     * 删除桌台类型
     *
     * @param typeIds 需要删除的桌台类型ID
     * @return 结果
     */
    @Override
    public int deleteTableTypeByIds(Long[] typeIds) {
        return tableTypeMapper.deleteTableTypeByIds(typeIds);
    }

    /**
     * 校验类型编码唯一性
     */
    @Override
    public boolean checkTypeCodeUnique(String typeCode) {
        SysTableType type = tableTypeMapper.checkTypeCodeUnique(typeCode);
        return type == null;
    }
}
