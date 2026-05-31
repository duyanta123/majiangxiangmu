package com.ruoyi.system.service;

import org.springframework.stereotype.Service;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.HashMap;
import java.util.Map;

@Service
public class AiIntentRecognitionService {
    
    public static class IntentResult {
        public String intent;
        public Map<String, Object> entities;
        public boolean needDatabase;
        public boolean needKnowledge;
        public String queryType;
        
        public IntentResult() {
            this.entities = new HashMap<>();
            this.needDatabase = false;
            this.needKnowledge = false;
        }
    }
    
    public IntentResult recognize(String question) {
        IntentResult result = new IntentResult();
        
        if (question == null || question.trim().isEmpty()) {
            result.intent = "unknown";
            return result;
        }
        
        String q = question.toLowerCase();
        
        if (containsAny(q, "消费", "累计消费", "花了多少钱", "我的消费")) {
            result.intent = "query_consumption";
            result.needDatabase = true;
            result.queryType = "consumption";
            result.entities.put("playerId", extractPlayerId(question));
            result.entities.put("playerName", extractPlayerName(question));
        }
        else if (containsAny(q, "积分", "可用积分", "累计积分", "我的积分")) {
            result.intent = "query_points";
            result.needDatabase = true;
            result.queryType = "points";
            result.entities.put("playerId", extractPlayerId(question));
            result.entities.put("playerName", extractPlayerName(question));
        }
        else if (containsAny(q, "预约", "订单", "预约订单", "我的预约")) {
            result.intent = "query_reservation";
            result.needDatabase = true;
            result.queryType = "reservation";
            result.entities.put("playerId", extractPlayerId(question));
        }
        else if (containsAny(q, "会员权益", "会员有什么优惠", "金卡权益", "银卡权益")) {
            result.intent = "query_member_benefits";
            result.needKnowledge = true;
        }
        else if (containsAny(q, "会员", "会员等级", "我的等级", "怎么办会员")) {
            result.intent = "query_member_info";
            result.needDatabase = true;
            result.queryType = "member";
            result.entities.put("playerId", extractPlayerId(question));
        }
        else if (containsAny(q, "桌台", "空闲", "空桌")) {
            result.intent = "query_table";
            result.needDatabase = true;
            result.queryType = "table";
        }
        else if (containsAny(q, "营收", "收入", "今日营收", "本月营收", "营业额")) {
            result.intent = "query_revenue";
            result.needDatabase = true;
            result.queryType = "revenue";
        }
        else if (containsAny(q, "营业时间", "开门", "关门", "几点", "几点营业")) {
            result.intent = "query_business_hours";
            result.needKnowledge = true;
        }
        else if (containsAny(q, "会员权益", "折扣", "优惠", "打折")) {
            result.intent = "query_member_benefits";
            result.needKnowledge = true;
        }
        else if (containsAny(q, "预约规则", "如何预约", "怎么预约")) {
            result.intent = "query_reservation_rules";
            result.needKnowledge = true;
        }
        else if (containsAny(q, "积分兑换", "积分规则", "怎么获得积分")) {
            result.intent = "query_points_rules";
            result.needKnowledge = true;
        }
        else if (containsAny(q, "套餐", "价格", "收费", "多少钱")) {
            result.intent = "query_pricing";
            result.needKnowledge = true;
        }
        else {
            result.intent = "general_chat";
            result.needKnowledge = true;
        }
        
        return result;
    }
    
    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    private Long extractPlayerId(String text) {
        Pattern pattern = Pattern.compile("(\\d+)\\s*号|玩家\\s*(\\d+)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                if (matcher.group(i) != null) {
                    try {
                        return Long.parseLong(matcher.group(i));
                    } catch (NumberFormatException e) {
                        // ignore
                    }
                }
            }
        }
        return null;
    }
    
    private String extractPlayerName(String text) {
        Pattern pattern = Pattern.compile("玩家[：:]*\\s*([^\\s,，。！？]+)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
