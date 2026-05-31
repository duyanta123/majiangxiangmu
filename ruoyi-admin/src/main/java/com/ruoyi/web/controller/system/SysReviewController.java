package com.ruoyi.web.controller.system;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.SysReview;
import com.ruoyi.system.service.ISysReviewService;

@RestController
@RequestMapping("/system/review")
public class SysReviewController extends BaseController {

    @Autowired
    private ISysReviewService reviewService;

    @PreAuthorize("@ss.hasPermi('system:review:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysReview review) {
        startPage();
        List<SysReview> list = reviewService.selectReviewList(review);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('system:review:query')")
    @GetMapping("/{reviewId}")
    public AjaxResult getInfo(@PathVariable Long reviewId) {
        return success(reviewService.selectReviewById(reviewId));
    }

    @PreAuthorize("@ss.hasPermi('system:review:add')")
    @Log(title = "门店评价", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysReview review) {
        return toAjax(reviewService.insertReview(review));
    }

    @PreAuthorize("@ss.hasPermi('system:review:edit')")
    @Log(title = "门店评价", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysReview review) {
        return toAjax(reviewService.updateReview(review));
    }

    @PreAuthorize("@ss.hasPermi('system:review:reply')")
    @Log(title = "评价回复", businessType = BusinessType.UPDATE)
    @PutMapping("/reply")
    public AjaxResult reply(@RequestBody SysReview review) {
        if (review.getReviewId() == null) {
            return AjaxResult.error("评价ID不能为空");
        }
        if (review.getReplyContent() == null || review.getReplyContent().trim().isEmpty()) {
            return AjaxResult.error("回复内容不能为空");
        }

        SysReview existingReview = reviewService.selectReviewById(review.getReviewId());
        if (existingReview == null) {
            return AjaxResult.error("评价不存在");
        }

        existingReview.setReplyContent(review.getReplyContent().trim());
        existingReview.setReplyTime(new Date());

        return toAjax(reviewService.updateReview(existingReview));
    }

    @PreAuthorize("@ss.hasPermi('system:review:remove')")
    @Log(title = "门店评价", businessType = BusinessType.DELETE)
    @DeleteMapping("/{reviewIds}")
    public AjaxResult remove(@PathVariable Long[] reviewIds) {
        return toAjax(reviewService.deleteReviewByIds(reviewIds));
    }
}
