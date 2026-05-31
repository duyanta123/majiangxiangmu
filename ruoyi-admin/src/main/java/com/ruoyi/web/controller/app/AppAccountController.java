package com.ruoyi.web.controller.app;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.SysConsume;
import com.ruoyi.system.service.ISysConsumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/app/account")
public class AppAccountController {

    @Autowired
    private ISysConsumeService sysConsumeService;

    @GetMapping("/consumption")
    public AjaxResult getMyConsumption() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return AjaxResult.error("用户未登录");
        }
        
        SysConsume consume = new SysConsume();
        consume.setPlayerId(loginUser.getUserId());
        List<SysConsume> list = sysConsumeService.selectConsumeList(consume);
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        int count = 0;
        
        for (SysConsume c : list) {
            if (c.getTotalAmount() != null) {
                totalAmount = totalAmount.add(c.getTotalAmount());
            }
            count++;
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("playerId", loginUser.getUserId());
        result.put("totalConsumption", totalAmount);
        result.put("consumptionCount", count);
        result.put("hasConsumption", count > 0);
        
        return AjaxResult.success(result);
    }

    @GetMapping("/consumption/records")
    public AjaxResult getMyConsumptionRecords() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return AjaxResult.error("用户未登录");
        }
        
        SysConsume consume = new SysConsume();
        consume.setPlayerId(loginUser.getUserId());
        List<SysConsume> list = sysConsumeService.selectConsumeList(consume);
        return AjaxResult.success().put("list", list);
    }
}
