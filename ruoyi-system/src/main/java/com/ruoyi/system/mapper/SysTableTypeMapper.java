package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysTableType;
import org.apache.ibatis.annotations.Param;

/**
 * 桌台类型Mapper接口
 *
 * @author ruoyi
 */
public interface SysTableTypeMapper {

    /**
     * 查询桌台类型
     *
     * @param typeId 桌台类型ID
     * @return 桌台类型
     */
    public SysTableType selectTableTypeById(Long typeId);

    /**
     * 查询桌台类型列表
     *
     * @param tableType 桌台类型
     * @return 桌台类型集合
     */
    public List<SysTableType> selectTableTypeList(SysTableType tableType);

    /**
     * 新增桌台类型
     *
     * @param tableType 桌台类型
     * @return 结果
     */
    public int insertTableType(SysTableType tableType);

    /**
     * 修改桌台类型
     *
     * @param tableType 桌台类型
     * @return 结果
     */
    public int updateTableType(SysTableType tableType);

    /**
     * 删除桌台类型
     *
     * @param typeIds 需要删除的桌台类型ID
     * @return 结果
     */
    public int deleteTableTypeByIds(Long[] typeIds);

    /**
     * 校验类型编码唯一性
     *
     * @param typeCode 类型编码
     * @return 桌台类型
     */
    public SysTableType checkTypeCodeUnique(@Param("typeCode") String typeCode);
}
