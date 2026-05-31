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
import com.ruoyi.system.domain.SysReservation;
import com.ruoyi.system.service.ISysReservationService;

@RestController
@RequestMapping("/system/reservation")
public class SysReservationController extends BaseController {

    @Autowired
    private ISysReservationService reservationService;

    @PreAuthorize("@ss.hasPermi('system:reservation:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysReservation reservation) {
        startPage();
        List<SysReservation> list = reservationService.selectReservationList(reservation);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('system:reservation:query')")
    @GetMapping("/{reservationId}")
    public AjaxResult getInfo(@PathVariable Long reservationId) {
        return success(reservationService.selectReservationById(reservationId));
    }

    @PreAuthorize("@ss.hasPermi('system:reservation:add')")
    @Log(title = "预约订单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysReservation reservation) {
        return toAjax(reservationService.insertReservation(reservation));
    }

    @PreAuthorize("@ss.hasPermi('system:reservation:edit')")
    @Log(title = "预约订单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysReservation reservation) {
        return toAjax(reservationService.updateReservation(reservation));
    }

    @PreAuthorize("@ss.hasPermi('system:reservation:remove')")
    @Log(title = "预约订单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{reservationIds}")
    public AjaxResult remove(@PathVariable Long[] reservationIds) {
        return toAjax(reservationService.deleteReservationByIds(reservationIds));
    }
}
