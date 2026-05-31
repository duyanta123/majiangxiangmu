package com.ruoyi.web.controller.app;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.SysConsume;
import com.ruoyi.system.service.ISysConsumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/consume")
public class AppConsumeController {

    @Autowired
    private ISysConsumeService sysConsumeService;

    @GetMapping("/test")
    public AjaxResult test() {
        return AjaxResult.success("Consume Controller Test OK");
    }

    @PostMapping("/add")
    public AjaxResult addConsumption(@RequestBody SysConsume consume) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return AjaxResult.error("用户未登录");
        }
        
        consume.setPlayerId(loginUser.getUserId());
        consume.setPayStatus("1");
        
        int result = sysConsumeService.insertConsume(consume);
        if (result > 0) {
            return AjaxResult.success("消费记录创建成功", consume);
        } else {
            return AjaxResult.error("消费记录创建失败");
        }
    }

    @GetMapping("/list")
    public AjaxResult getConsumeList() {
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
