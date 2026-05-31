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
import com.ruoyi.system.domain.SysStore;
import com.ruoyi.system.service.ISysStoreService;

/**
 * 门店管理Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/store")
public class SysStoreController extends BaseController {

    @Autowired
    private ISysStoreService storeService;

    /**
     * 获取门店列表
     */
    @PreAuthorize("@ss.hasPermi('system:store:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysStore store) {
        startPage();
        List<SysStore> list = storeService.selectStoreList(store);
        return getDataTable(list);
    }

    /**
     * 获取所有门店（下拉选择用）
     */
    @GetMapping("/all")
    public AjaxResult getAllStores() {
        return success(storeService.selectStoreList(new SysStore()));
    }

    /**
     * 导出门店列表
     */
    @PreAuthorize("@ss.hasPermi('system:store:export')")
    @Log(title = "门店管理", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(SysStore store) {
        List<SysStore> list = storeService.selectStoreList(store);
        ExcelUtil<SysStore> util = new ExcelUtil<SysStore>(SysStore.class);
        return util.exportExcel(list, "门店数据");
    }

    /**
     * 获取门店详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:store:query')")
    @GetMapping("/{storeId}")
    public AjaxResult getInfo(@PathVariable("storeId") Long storeId) {
        return success(storeService.selectStoreById(storeId));
    }

    /**
     * 新增门店
     */
    @PreAuthorize("@ss.hasPermi('system:store:add')")
    @Log(title = "门店管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysStore store) {
        return toAjax(storeService.insertStore(store));
    }

    /**
     * 修改门店
     */
    @PreAuthorize("@ss.hasPermi('system:store:edit')")
    @Log(title = "门店管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysStore store) {
        return toAjax(storeService.updateStore(store));
    }

    /**
     * 删除门店
     */
    @PreAuthorize("@ss.hasPermi('system:store:remove')")
    @Log(title = "门店管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{storeIds}")
    public AjaxResult remove(@PathVariable Long[] storeIds) {
        return toAjax(storeService.deleteStoreByIds(storeIds));
    }
}
