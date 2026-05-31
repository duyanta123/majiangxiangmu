package com.ruoyi.web.controller.app;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/user")
public class AppUserController {

    @GetMapping("/info")
    public AjaxResult getUserInfo() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return AjaxResult.error("用户未登录");
        }
        SysUser user = loginUser.getUser();
        return AjaxResult.success("查询成功").put("user", user)
                .put("userId", user.getUserId())
                .put("username", user.getUserName())
                .put("nickname", user.getNickName());
    }

    @GetMapping("/consumption")
    public AjaxResult getMyConsumption() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return AjaxResult.error("用户未登录");
        }
        return AjaxResult.success("查询成功").put("playerId", loginUser.getUserId());
    }
}
