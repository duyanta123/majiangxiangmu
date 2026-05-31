package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 会员信息对象 sys_member
 *
 * @author ruoyi
 */
public class SysMember extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long memberId;
    private Long playerId;
    private String playerName;
    private String playerPhone;
    private Long levelId;
    private String levelName;
    private Integer totalPoints;
    private Integer availablePoints;
    private BigDecimal totalConsumption;
    private Integer consumeCount;
    private String cardNo;
    private Date memberSince;
    private Date expireTime;
    private String status;
    private String delFlag;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    private String remark;

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public String getPlayerPhone() { return playerPhone; }
    public void setPlayerPhone(String playerPhone) { this.playerPhone = playerPhone; }
    public Long getLevelId() { return levelId; }
    public void setLevelId(Long levelId) { this.levelId = levelId; }
    public String getLevelName() { return levelName; }
    public void setLevelName(String levelName) { this.levelName = levelName; }
    public Integer getTotalPoints() { return totalPoints; }
    public void setTotalPoints(Integer totalPoints) { this.totalPoints = totalPoints; }
    public Integer getAvailablePoints() { return availablePoints; }
    public void setAvailablePoints(Integer availablePoints) { this.availablePoints = availablePoints; }
    public BigDecimal getTotalConsumption() { return totalConsumption; }
    public void setTotalConsumption(BigDecimal totalConsumption) { this.totalConsumption = totalConsumption; }
    public Integer getConsumeCount() { return consumeCount; }
    public void setConsumeCount(Integer consumeCount) { this.consumeCount = consumeCount; }
    public String getCardNo() { return cardNo; }
    public void setCardNo(String cardNo) { this.cardNo = cardNo; }
    public Date getMemberSince() { return memberSince; }
    public void setMemberSince(Date memberSince) { this.memberSince = memberSince; }
    public Date getExpireTime() { return expireTime; }
    public void setExpireTime(Date expireTime) { this.expireTime = expireTime; }
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
