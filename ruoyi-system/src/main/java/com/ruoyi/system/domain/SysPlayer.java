package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

public class SysPlayer extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long playerId;
    private String playerName;
    private String nickName;
    private String playerPhone;
    private String phoneNumber;
    private String playerAvatar;
    private String playerType;
    private Double totalConsumption;
    private String gender;
    private Date birthday;
    private String idCard;
    private String address;
    private Integer totalVisits;
    private Long totalSpent;
    private String preferredStore;
    private String status;
    private String delFlag;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    private String remark;

    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public String getNickName() { return nickName; }
    public void setNickName(String nickName) { this.nickName = nickName; }
    public String getPlayerPhone() { return playerPhone; }
    public void setPlayerPhone(String playerPhone) { this.playerPhone = playerPhone; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getPlayerAvatar() { return playerAvatar; }
    public void setPlayerAvatar(String playerAvatar) { this.playerAvatar = playerAvatar; }
    public String getPlayerType() { return playerType; }
    public void setPlayerType(String playerType) { this.playerType = playerType; }
    public Double getTotalConsumption() { return totalConsumption; }
    public void setTotalConsumption(Double totalConsumption) { this.totalConsumption = totalConsumption; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public Date getBirthday() { return birthday; }
    public void setBirthday(Date birthday) { this.birthday = birthday; }
    public String getIdCard() { return idCard; }
    public void setIdCard(String idCard) { this.idCard = idCard; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Integer getTotalVisits() { return totalVisits; }
    public void setTotalVisits(Integer totalVisits) { this.totalVisits = totalVisits; }
    public Long getTotalSpent() { return totalSpent; }
    public void setTotalSpent(Long totalSpent) { this.totalSpent = totalSpent; }
    public String getPreferredStore() { return preferredStore; }
    public void setPreferredStore(String preferredStore) { this.preferredStore = preferredStore; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
