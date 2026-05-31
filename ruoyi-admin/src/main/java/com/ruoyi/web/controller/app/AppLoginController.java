package com.ruoyi.web.controller.app;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.framework.security.context.AuthenticationContextHolder;
import com.ruoyi.framework.web.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/app")
@Anonymous
public class AppLoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public AjaxResult login(@RequestBody Map<String, String> loginBody) {
        String username = loginBody.get("username");
        String password = loginBody.get("password");
        
        if (username == null || username.trim().isEmpty()) {
            return AjaxResult.error("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            return AjaxResult.error("密码不能为空");
        }
        UsernamePasswordAuthenticationToken authenticationToken = 
                new UsernamePasswordAuthenticationToken(username, password);
        AuthenticationContextHolder.setContext(authenticationToken);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        AuthenticationContextHolder.clearContext();
        
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        String token = tokenService.createToken(loginUser);
        return AjaxResult.success("登录成功").put(Constants.TOKEN, token);
    }

    @PostMapping("/register")
    public AjaxResult register(@RequestBody Map<String, String> registerBody) {
        String username = registerBody.get("username");
        String password = registerBody.get("password");
        String nickname = registerBody.get("nickname");
        
        if (username == null || username.trim().isEmpty()) {
            return AjaxResult.error("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            return AjaxResult.error("密码不能为空");
        }
        if (nickname == null || nickname.trim().isEmpty()) {
            return AjaxResult.error("昵称不能为空");
        }
        return AjaxResult.success("注册成功");
    }
}
