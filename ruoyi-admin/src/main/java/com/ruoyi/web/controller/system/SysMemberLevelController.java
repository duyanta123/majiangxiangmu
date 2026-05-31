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
import com.ruoyi.system.domain.SysMemberLevel;
import com.ruoyi.system.service.ISysMemberLevelService;

@RestController
@RequestMapping("/system/memberLevel")
public class SysMemberLevelController extends BaseController {

    @Autowired
    private ISysMemberLevelService levelService;

    @PreAuthorize("@ss.hasPermi('system:memberLevel:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysMemberLevel level) {
        startPage();
        List<SysMemberLevel> list = levelService.selectMemberLevelList(level);
        return getDataTable(list);
    }

    @GetMapping("/all")
    public AjaxResult getAllLevels() {
        return success(levelService.selectMemberLevelList(new SysMemberLevel()));
    }

    @PreAuthorize("@ss.hasPermi('system:memberLevel:query')")
    @GetMapping("/{levelId}")
    public AjaxResult getInfo(@PathVariable Long levelId) {
        return success(levelService.selectMemberLevelById(levelId));
    }

    @PreAuthorize("@ss.hasPermi('system:memberLevel:add')")
    @Log(title = "会员等级", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysMemberLevel level) {
        if (!levelService.checkLevelCodeUnique(level.getLevelCode())) {
            return error("等级编码已存在");
        }
        return toAjax(levelService.insertMemberLevel(level));
    }

    @PreAuthorize("@ss.hasPermi('system:memberLevel:edit')")
    @Log(title = "会员等级", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysMemberLevel level) {
        return toAjax(levelService.updateMemberLevel(level));
    }

    @PreAuthorize("@ss.hasPermi('system:memberLevel:remove')")
    @Log(title = "会员等级", businessType = BusinessType.DELETE)
    @DeleteMapping("/{levelIds}")
    public AjaxResult remove(@PathVariable Long[] levelIds) {
        return toAjax(levelService.deleteMemberLevelByIds(levelIds));
    }
}
