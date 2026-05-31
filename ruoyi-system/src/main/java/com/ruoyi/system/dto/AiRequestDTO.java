package com.ruoyi.system.dto;

public class AiRequestDTO {
    private Long storeId;
    private Long playerId;
    private String input;
    private String type;

    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }
    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }
    public String getInput() { return input; }
    public void setInput(String input) { this.input = input; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
