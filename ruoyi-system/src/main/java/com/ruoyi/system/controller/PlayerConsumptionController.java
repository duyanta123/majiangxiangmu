package com.ruoyi.system.controller;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.PlayerConsumptionQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

@RestController
@RequestMapping("/system/ai/consumption")
@Anonymous
public class PlayerConsumptionController {
    
    private static final Logger log = LoggerFactory.getLogger(PlayerConsumptionController.class);
    
    @Autowired
    private PlayerConsumptionQueryService consumptionQueryService;
    
    @PostMapping("/query")
    public AjaxResult query(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        
        if (question == null || question.trim().isEmpty()) {
            return error("问题不能为空");
        }
        
        log.info("查询玩家消费请求 - 问题: {}", question);
        
        try {
            PlayerConsumptionQueryService.ConsumptionResult result = 
                    consumptionQueryService.queryPlayerConsumption(question);
            
            if (result.success) {
                return success(result.data);
            } else {
                return error(result.message);
            }
            
        } catch (Exception e) {
            log.error("查询玩家消费异常: {}", e.getMessage(), e);
            return error("查询失败: " + e.getMessage());
        }
    }
    
    protected AjaxResult success(Object data) {
        return AjaxResult.success(data);
    }
    
    protected AjaxResult error(String message) {
        return AjaxResult.error(message);
    }
}
