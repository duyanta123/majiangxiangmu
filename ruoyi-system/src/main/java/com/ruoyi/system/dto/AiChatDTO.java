package com.ruoyi.system.dto;

import java.util.List;
import java.util.Map;

public class AiChatDTO {
    private Long playerId;
    private String playerName;
    private String playerPhone;
    private String userInput;
    private String aiResponse;
    private String intent;
    private String chatType;
    private List<Map<String, String>> history;
    
    private String memberLevel;
    private Double totalConsumption;
    private Integer points;
    private Integer consumeCount;

    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public String getPlayerPhone() { return playerPhone; }
    public void setPlayerPhone(String playerPhone) { this.playerPhone = playerPhone; }
    public String getUserInput() { return userInput; }
    public void setUserInput(String userInput) { this.userInput = userInput; }
    public String getAiResponse() { return aiResponse; }
    public void setAiResponse(String aiResponse) { this.aiResponse = aiResponse; }
    public String getIntent() { return intent; }
    public void setIntent(String intent) { this.intent = intent; }
    public String getChatType() { return chatType; }
    public void setChatType(String chatType) { this.chatType = chatType; }
    public List<Map<String, String>> getHistory() { return history; }
    public void setHistory(List<Map<String, String>> history) { this.history = history; }
    
    public String getMemberLevel() { return memberLevel; }
    public void setMemberLevel(String memberLevel) { this.memberLevel = memberLevel; }
    public Double getTotalConsumption() { return totalConsumption; }
    public void setTotalConsumption(Double totalConsumption) { this.totalConsumption = totalConsumption; }
    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }
    public Integer getConsumeCount() { return consumeCount; }
    public void setConsumeCount(Integer consumeCount) { this.consumeCount = consumeCount; }
}
