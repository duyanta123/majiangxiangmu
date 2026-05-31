package com.ruoyi.system.domain;

import java.util.Date;

public class SysAiRecommendation {
    private Long recId;
    private Long playerId;
    private String playerName;
    private String playerPhone;
    private String recType;
    private String recContent;
    private String recReason;
    private String status;
    private Date createTime;
    private Date expireTime;

    public Long getRecId() { return recId; }
    public void setRecId(Long recId) { this.recId = recId; }
    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public String getPlayerPhone() { return playerPhone; }
    public void setPlayerPhone(String playerPhone) { this.playerPhone = playerPhone; }
    public String getRecType() { return recType; }
    public void setRecType(String recType) { this.recType = recType; }
    public String getRecContent() { return recContent; }
    public void setRecContent(String recContent) { this.recContent = recContent; }
    public String getRecReason() { return recReason; }
    public void setRecReason(String recReason) { this.recReason = recReason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getExpireTime() { return expireTime; }
    public void setExpireTime(Date expireTime) { this.expireTime = expireTime; }
}
