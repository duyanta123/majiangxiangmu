package com.ruoyi.web.controller.system;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.SysPointsLog;
import com.ruoyi.system.service.ISysPointsLogService;

@RestController
@RequestMapping("/system/pointsLog")
public class SysPointsLogController extends BaseController {

    @Autowired
    private ISysPointsLogService pointsLogService;

    @PreAuthorize("@ss.hasPermi('system:pointsLog:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysPointsLog log) {
        startPage();
        List<SysPointsLog> list = pointsLogService.selectPointsLogList(log);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('system:pointsLog:query')")
    @GetMapping("/{logId}")
    public AjaxResult getInfo(@PathVariable Long logId) {
        return success(pointsLogService.selectPointsLogById(logId));
    }

    @PreAuthorize("@ss.hasPermi('system:pointsLog:remove')")
    @Log(title = "积分记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{logIds}")
    public AjaxResult remove(@PathVariable Long[] logIds) {
        return toAjax(pointsLogService.deletePointsLogByIds(logIds));
    }
}
