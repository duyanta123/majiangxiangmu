package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysTableInfo;
import org.apache.ibatis.annotations.Param;

/**
 * 桌台信息Mapper接口
 *
 * @author ruoyi
 */
public interface SysTableInfoMapper {

    /**
     * 查询桌台信息
     *
     * @param tableId 桌台ID
     * @return 桌台信息
     */
    public SysTableInfo selectTableInfoById(Long tableId);

    /**
     * 查询桌台信息列表
     *
     * @param tableInfo 桌台信息
     * @return 桌台信息集合
     */
    public List<SysTableInfo> selectTableInfoList(SysTableInfo tableInfo);

    /**
     * 新增桌台信息
     *
     * @param tableInfo 桌台信息
     * @return 结果
     */
    public int insertTableInfo(SysTableInfo tableInfo);

    /**
     * 修改桌台信息
     *
     * @param tableInfo 桌台信息
     * @return 结果
     */
    public int updateTableInfo(SysTableInfo tableInfo);

    /**
     * 删除桌台信息
     *
     * @param tableIds 需要删除的桌台ID
     * @return 结果
     */
    public int deleteTableInfoByIds(Long[] tableIds);

    /**
     * 校验桌台编号唯一性
     *
     * @param tableCode 桌台编号
     * @return 桌台信息
     */
    public SysTableInfo checkTableCodeUnique(@Param("tableCode") String tableCode);

    /**
     * 更新桌台状态
     *
     * @param tableId 桌台ID
     * @param tableStatus 桌台状态
     * @return 结果
     */
    public int updateTableStatus(@Param("tableId") Long tableId, @Param("tableStatus") String tableStatus);
}
