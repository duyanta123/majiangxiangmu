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
import com.ruoyi.system.domain.SysMember;
import com.ruoyi.system.service.ISysMemberService;

@RestController
@RequestMapping("/system/member")
public class SysMemberController extends BaseController {

    @Autowired
    private ISysMemberService memberService;

    @PreAuthorize("@ss.hasPermi('system:member:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysMember member) {
        startPage();
        List<SysMember> list = memberService.selectMemberList(member);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('system:member:query')")
    @GetMapping("/{memberId}")
    public AjaxResult getInfo(@PathVariable Long memberId) {
        return success(memberService.selectMemberById(memberId));
    }

    @PreAuthorize("@ss.hasPermi('system:member:add')")
    @Log(title = "会员管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysMember member) {
        return toAjax(memberService.insertMember(member));
    }

    @PreAuthorize("@ss.hasPermi('system:member:edit')")
    @Log(title = "会员管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysMember member) {
        return toAjax(memberService.updateMember(member));
    }

    @PreAuthorize("@ss.hasPermi('system:member:remove')")
    @Log(title = "会员管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{memberIds}")
    public AjaxResult remove(@PathVariable Long[] memberIds) {
        return toAjax(memberService.deleteMemberByIds(memberIds));
    }

    @PreAuthorize("@ss.hasPermi('system:member:addPoints')")
    @Log(title = "会员管理", businessType = BusinessType.UPDATE)
    @PostMapping("/addPoints/{memberId}/{points}")
    public AjaxResult addPoints(@PathVariable Long memberId, @PathVariable Integer points, @RequestParam String description) {
        int result = memberService.addPoints(memberId, points, "MANUAL", null, description);
        if (result > 0) return success("积分增加成功");
        return error("操作失败");
    }

    @PreAuthorize("@ss.hasPermi('system:member:deductPoints')")
    @Log(title = "会员管理", businessType = BusinessType.UPDATE)
    @PostMapping("/deductPoints/{memberId}/{points}")
    public AjaxResult deductPoints(@PathVariable Long memberId, @PathVariable Integer points, @RequestParam String description) {
        int result = memberService.deductPoints(memberId, points, "MANUAL", null, description);
        if (result > 0) return success("积分扣减成功");
        if (result == -1) return error("积分不足");
        return error("操作失败");
    }
}
