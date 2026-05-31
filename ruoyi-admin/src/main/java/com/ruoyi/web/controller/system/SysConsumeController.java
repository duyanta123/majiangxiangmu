package com.ruoyi.web.controller.system;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.SysConsume;
import com.ruoyi.system.service.ISysConsumeService;

/**
 * 消费记录管理
 */
@RestController
@RequestMapping("/system/consume")
public class SysConsumeController extends BaseController {

    @Autowired
    private ISysConsumeService sysConsumeService;

    @PreAuthorize("@ss.hasPermi('system:consume:list')")
    @GetMapping("/list")
    public AjaxResult list(SysConsume consume) {
        List<SysConsume> list = sysConsumeService.selectConsumeList(consume);
        return success(list);
    }

    @PreAuthorize("@ss.hasPermi('system:consume:query')")
    @GetMapping("/{consumeId}")
    public AjaxResult getInfo(@PathVariable Long consumeId) {
        return success(sysConsumeService.selectConsumeById(consumeId));
    }

    @PreAuthorize("@ss.hasPermi('system:consume:add')")
    @PostMapping
    public AjaxResult add(@RequestBody SysConsume consume) {
        return toAjax(sysConsumeService.insertConsume(consume));
    }

    @PreAuthorize("@ss.hasPermi('system:consume:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody SysConsume consume) {
        return toAjax(sysConsumeService.updateConsume(consume));
    }

    @PreAuthorize("@ss.hasPermi('system:consume:remove')")
    @DeleteMapping("/{consumeIds}")
    public AjaxResult remove(@PathVariable Long[] consumeIds) {
        return toAjax(sysConsumeService.deleteConsumeByIds(consumeIds));
    }
}