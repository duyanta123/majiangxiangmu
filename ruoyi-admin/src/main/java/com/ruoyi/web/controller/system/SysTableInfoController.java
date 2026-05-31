package com.ruoyi.web.controller.system;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.SysTableInfo;
import com.ruoyi.system.service.ISysTableInfoService;

/**
 * 桌台信息Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/tableInfo")
public class SysTableInfoController extends BaseController {

    @Autowired
    private ISysTableInfoService tableInfoService;

    /**
     * 获取桌台信息列表
     */
    @PreAuthorize("@ss.hasPermi('system:tableInfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysTableInfo tableInfo) {
        startPage();
        List<SysTableInfo> list = tableInfoService.selectTableInfoList(tableInfo);
        return getDataTable(list);
    }

    /**
     * 获取所有桌台信息
     */
    @GetMapping("/all")
    public AjaxResult getAllTables() {
        return success(tableInfoService.selectTableInfoList(new SysTableInfo()));
    }

    /**
     * 导出台台信息列表
     */
    @PreAuthorize("@ss.hasPermi('system:tableInfo:export')")
    @Log(title = "桌台信息", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(SysTableInfo tableInfo) {
        List<SysTableInfo> list = tableInfoService.selectTableInfoList(tableInfo);
        ExcelUtil<SysTableInfo> util = new ExcelUtil<SysTableInfo>(SysTableInfo.class);
        return util.exportExcel(list, "桌台信息数据");
    }

    /**
     * 获取桌台信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:tableInfo:query')")
    @GetMapping("/{tableId}")
    public AjaxResult getInfo(@PathVariable("tableId") Long tableId) {
        return success(tableInfoService.selectTableInfoById(tableId));
    }

    /**
     * 新增桌台信息
     */
    @PreAuthorize("@ss.hasPermi('system:tableInfo:add')")
    @Log(title = "桌台信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysTableInfo tableInfo) {
        if (!tableInfoService.checkTableCodeUnique(tableInfo.getTableCode())) {
            return error("桌台编号已存在");
        }
        return toAjax(tableInfoService.insertTableInfo(tableInfo));
    }

    /**
     * 修改桌台信息
     */
    @PreAuthorize("@ss.hasPermi('system:tableInfo:edit')")
    @Log(title = "桌台信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysTableInfo tableInfo) {
        return toAjax(tableInfoService.updateTableInfo(tableInfo));
    }

    /**
     * 删除桌台信息
     */
    @PreAuthorize("@ss.hasPermi('system:tableInfo:remove')")
    @Log(title = "桌台信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{tableIds}")
    public AjaxResult remove(@PathVariable Long[] tableIds) {
        return toAjax(tableInfoService.deleteTableInfoByIds(tableIds));
    }

    /**
     * 更新桌台状态
     */
    @PutMapping("/status/{tableId}/{tableStatus}")
    public AjaxResult updateStatus(@PathVariable Long tableId, @PathVariable String tableStatus) {
        return toAjax(tableInfoService.updateTableStatus(tableId, tableStatus));
    }
}
