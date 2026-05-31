package com.ruoyi.system.controller;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.AiAgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

@RestController
@RequestMapping("/system/ai/agent")
@Anonymous
public class AiAgentController {
    
    private static final Logger log = LoggerFactory.getLogger(AiAgentController.class);
    
    @Autowired
    private AiAgentService agentService;
    
    @PostMapping("/chat")
    public AjaxResult chat(@RequestBody Map<String, Object> request) {
        String question = (String) request.get("question");
        String chatHistory = (String) request.get("chatHistory");
        
        if (question == null || question.trim().isEmpty()) {
            return error("问题不能为空");
        }
        
        log.info("AI Agent聊天请求 - 问题: {}", question);
        
        try {
            AiAgentService.AgentResponse response = agentService.process(question, chatHistory);
            
            Map<String, Object> data = new java.util.HashMap<>();
            data.put("response", response.response);
            data.put("intent", response.intent);
            data.put("fromDatabase", response.fromDatabase);
            data.put("fromKnowledge", response.fromKnowledge);
            data.put("success", response.success);
            data.put("metadata", response.metadata);
            
            return success(data);
            
        } catch (Exception e) {
            log.error("Agent聊天异常: {}", e.getMessage(), e);
            return error("处理失败: " + e.getMessage());
        }
    }
    
    protected AjaxResult success(Object data) {
        return AjaxResult.success(data);
    }
    
    protected AjaxResult error(String message) {
        return AjaxResult.error(message);
    }
}
