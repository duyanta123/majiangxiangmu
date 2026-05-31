package com.ruoyi.system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

@Service
public class AiAgentService {
    
    private static final Logger log = LoggerFactory.getLogger(AiAgentService.class);
    
    @Autowired
    private AiIntentRecognitionService intentRecognition;
    
    @Autowired
    private PlayerConsumptionQueryService consumptionQueryService;
    
    @Autowired
    private DeepSeekService deepSeekService;
    
    @Autowired
    private AiKnowledgeService knowledgeService;
    
    public static class AgentResponse {
        public boolean success;
        public String response;
        public String intent;
        public boolean fromDatabase;
        public boolean fromKnowledge;
        public Map<String, Object> metadata;
        
        public AgentResponse() {
            this.metadata = new HashMap<>();
            this.fromDatabase = false;
            this.fromKnowledge = false;
        }
    }
    
    public AgentResponse process(String question, String chatHistory) {
        log.info("AI Agent处理请求 - 问题: {}", question);
        
        AgentResponse response = new AgentResponse();
        
        try {
            AiIntentRecognitionService.IntentResult intent = intentRecognition.recognize(question);
            response.intent = intent.intent;
            
            log.info("意图识别结果 - intent: {}, needDatabase: {}, needKnowledge: {}", 
                    intent.intent, intent.needDatabase, intent.needKnowledge);
            
            if (intent.needDatabase) {
                return handleDatabaseIntent(question, intent, response);
            }
            
            if (intent.needKnowledge) {
                return handleKnowledgeIntent(question, response);
            }
            
            return handleGeneralChat(question, chatHistory, response);
            
        } catch (Exception e) {
            log.error("Agent处理异常: {}", e.getMessage(), e);
            response.success = false;
            response.response = "抱歉，处理您的请求时出现错误，请稍后再试。";
            return response;
        }
    }
    
    private AgentResponse handleDatabaseIntent(String question, 
            AiIntentRecognitionService.IntentResult intent, AgentResponse response) {
        
        log.info("处理数据库查询意图 - queryType: {}", intent.queryType);
        
        if ("consumption".equals(intent.queryType)) {
            PlayerConsumptionQueryService.ConsumptionResult result = 
                    consumptionQueryService.queryPlayerConsumption(question);
            
            if (result.success) {
                String naturalLanguage = consumptionQueryService.formatNaturalLanguage(result);
                response.success = true;
                response.response = naturalLanguage;
                response.fromDatabase = true;
                response.metadata.put("playerId", result.data.get("playerId"));
                response.metadata.put("totalConsumption", result.data.get("totalConsumption"));
            } else {
                response.success = false;
                response.response = "抱歉，" + result.message;
            }
            return response;
        }
        
        response.success = true;
        response.response = "该功能正在开发中，敬请期待。";
        return response;
    }
    
    private AgentResponse handleKnowledgeIntent(String question, AgentResponse response) {
        log.info("处理知识库查询意图");
        
        try {
            String knowledgeContext = knowledgeService.getKnowledgeContext(question);
            
            String prompt = buildPromptWithKnowledge(question, knowledgeContext);
            
            Map<String, Object> playerInfo = new HashMap<>();
            Map<String, String> context = new HashMap<>();
            String aiResponse = deepSeekService.chat(prompt, playerInfo, context);
            
            response.success = true;
            response.response = aiResponse;
            response.fromKnowledge = true;
            response.metadata.put("knowledgeUsed", true);
            
        } catch (Exception e) {
            log.error("知识库查询失败: {}", e.getMessage(), e);
            return handleGeneralChat(question, null, response);
        }
        
        return response;
    }
    
    private AgentResponse handleGeneralChat(String question, String chatHistory, AgentResponse response) {
        log.info("处理通用对话");
        
        try {
            String prompt = buildPromptWithHistory(question, chatHistory);
            
            Map<String, Object> playerInfo = new HashMap<>();
            Map<String, String> context = new HashMap<>();
            String aiResponse = deepSeekService.chat(prompt, playerInfo, context);
            
            response.success = true;
            response.response = aiResponse;
            
        } catch (Exception e) {
            log.error("通用对话失败: {}", e.getMessage(), e);
            response.success = false;
            response.response = "抱歉，AI服务暂时不可用，请稍后再试。";
        }
        
        return response;
    }
    
    private String buildPromptWithKnowledge(String question, String knowledgeContext) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("你是爱麻社的AI智能助手，专门为麻将馆顾客提供咨询服务。\n\n");
        
        if (knowledgeContext != null && !knowledgeContext.isEmpty()) {
            prompt.append("【相关知识】\n").append(knowledgeContext).append("\n\n");
        }
        
        prompt.append("【用户问题】\n").append(question).append("\n\n");
        
        prompt.append("请根据相关知识，准确回答用户问题。如果知识库中没有相关信息，请基于实际情况给出合理建议。");
        
        return prompt.toString();
    }
    
    private String buildPromptWithHistory(String question, String chatHistory) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("你是爱麻社的AI智能助手，专门为麻将馆顾客提供咨询服务。\n\n");
        
        if (chatHistory != null && !chatHistory.isEmpty()) {
            prompt.append("【对话历史】\n").append(chatHistory).append("\n\n");
        }
        
        prompt.append("【当前问题】\n").append(question);
        
        return prompt.toString();
    }
}
