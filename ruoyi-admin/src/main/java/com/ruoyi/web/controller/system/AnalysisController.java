package com.ruoyi.web.controller.system;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.dto.AnalysisDTO;
import com.ruoyi.system.dto.RevenueStats;
import com.ruoyi.system.dto.MemberStats;
import com.ruoyi.system.service.IAnalysisService;

@RestController
@RequestMapping("/system/analysis")
public class AnalysisController extends BaseController {

    @Autowired
    private IAnalysisService analysisService;

    @PreAuthorize("@ss.hasPermi('system:analysis:dashboard')")
    @GetMapping("/dashboard")
    public AjaxResult dashboard() {
        return success(analysisService.getDashboard());
    }

    @PreAuthorize("@ss.hasPermi('system:analysis:revenue')")
    @GetMapping("/revenue")
    public AjaxResult revenueTrend(AnalysisDTO dto) {
        return success(analysisService.getRevenueTrend(dto));
    }

    @PreAuthorize("@ss.hasPermi('system:analysis:member')")
    @GetMapping("/member")
    public AjaxResult memberTrend(AnalysisDTO dto) {
        return success(analysisService.getMemberTrend(dto));
    }

    @PreAuthorize("@ss.hasPermi('system:analysis:store')")
    @GetMapping("/store/{storeId}")
    public AjaxResult storeStats(@PathVariable Long storeId) {
        return success(analysisService.getStoreAnalysis(storeId));
    }

    @PreAuthorize("@ss.hasPermi('system:analysis:ranking')")
    @GetMapping("/ranking")
    public AjaxResult memberRanking(@RequestParam(defaultValue = "10") Integer limit) {
        return success(analysisService.getConsumeRanking(limit));
    }

    @PreAuthorize("@ss.hasPermi('system:analysis:table')")
    @GetMapping("/table")
    public AjaxResult tableTypeStats(Long storeId) {
        return success(analysisService.getTableTypeRevenue(storeId));
    }
}
