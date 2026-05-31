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
import com.ruoyi.system.domain.SysTableType;
import com.ruoyi.system.service.ISysTableTypeService;

/**
 * 桌台类型Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/tableType")
public class SysTableTypeController extends BaseController {

    @Autowired
    private ISysTableTypeService tableTypeService;

    /**
     * 获取桌台类型列表
     */
    @PreAuthorize("@ss.hasPermi('system:tableType:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysTableType tableType) {
        startPage();
        List<SysTableType> list = tableTypeService.selectTableTypeList(tableType);
        return getDataTable(list);
    }

    /**
     * 获取所有桌台类型
     */
    @GetMapping("/all")
    public AjaxResult getAllTypes() {
        return success(tableTypeService.selectTableTypeList(new SysTableType()));
    }

    /**
     * 导出桌台类型列表
     */
    @PreAuthorize("@ss.hasPermi('system:tableType:export')")
    @Log(title = "桌台类型", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(SysTableType tableType) {
        List<SysTableType> list = tableTypeService.selectTableTypeList(tableType);
        ExcelUtil<SysTableType> util = new ExcelUtil<SysTableType>(SysTableType.class);
        return util.exportExcel(list, "桌台类型数据");
    }

    /**
     * 获取桌台类型详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:tableType:query')")
    @GetMapping("/{typeId}")
    public AjaxResult getInfo(@PathVariable("typeId") Long typeId) {
        return success(tableTypeService.selectTableTypeById(typeId));
    }

    /**
     * 新增桌台类型
     */
    @PreAuthorize("@ss.hasPermi('system:tableType:add')")
    @Log(title = "桌台类型", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysTableType tableType) {
        if (!tableTypeService.checkTypeCodeUnique(tableType.getTypeCode())) {
            return error("类型编码已存在");
        }
        return toAjax(tableTypeService.insertTableType(tableType));
    }

    /**
     * 修改桌台类型
     */
    @PreAuthorize("@ss.hasPermi('system:tableType:edit')")
    @Log(title = "桌台类型", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysTableType tableType) {
        return toAjax(tableTypeService.updateTableType(tableType));
    }

    /**
     * 删除桌台类型
     */
    @PreAuthorize("@ss.hasPermi('system:tableType:remove')")
    @Log(title = "桌台类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{typeIds}")
    public AjaxResult remove(@PathVariable Long[] typeIds) {
        return toAjax(tableTypeService.deleteTableTypeByIds(typeIds));
    }
}
