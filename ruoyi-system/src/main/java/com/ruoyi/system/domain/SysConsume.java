package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 消费记录对象 sys_consume
 *
 * @author ruoyi
 */
public class SysConsume extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long consumeId;
    private String consumeNo;
    private Long playerId;
    private String playerName;
    private Long reservationId;
    private String reservationCode;
    private Long storeId;
    private String storeName;
    private Long tableId;
    private String tableName;
    private BigDecimal tableFee;
    private BigDecimal packageFee;
    private BigDecimal otherFee;
    private BigDecimal totalAmount;
    private String consumeType;
    private String payType;
    private String payStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date payTime;
    private String remark;
    private String delFlag;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    
    private String verificationCode;
    private String verificationStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date verificationTime;
    private String verifierId;
    private String verifierName;

    public Long getConsumeId() { return consumeId; }
    public void setConsumeId(Long consumeId) { this.consumeId = consumeId; }
    public String getConsumeNo() { return consumeNo; }
    public void setConsumeNo(String consumeNo) { this.consumeNo = consumeNo; }
    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }
    public String getReservationCode() { return reservationCode; }
    public void setReservationCode(String reservationCode) { this.reservationCode = reservationCode; }
    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }
    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }
    public Long getTableId() { return tableId; }
    public void setTableId(Long tableId) { this.tableId = tableId; }
    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }
    public BigDecimal getTableFee() { return tableFee; }
    public void setTableFee(BigDecimal tableFee) { this.tableFee = tableFee; }
    public BigDecimal getPackageFee() { return packageFee; }
    public void setPackageFee(BigDecimal packageFee) { this.packageFee = packageFee; }
    public BigDecimal getOtherFee() { return otherFee; }
    public void setOtherFee(BigDecimal otherFee) { this.otherFee = otherFee; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getConsumeType() { return consumeType; }
    public void setConsumeType(String consumeType) { this.consumeType = consumeType; }
    public String getPayType() { return payType; }
    public void setPayType(String payType) { this.payType = payType; }
    public String getPayStatus() { return payStatus; }
    public void setPayStatus(String payStatus) { this.payStatus = payStatus; }
    public Date getPayTime() { return payTime; }
    public void setPayTime(Date payTime) { this.payTime = payTime; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
    
    public String getVerificationCode() { return verificationCode; }
    public void setVerificationCode(String verificationCode) { this.verificationCode = verificationCode; }
    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }
    public Date getVerificationTime() { return verificationTime; }
    public void setVerificationTime(Date verificationTime) { this.verificationTime = verificationTime; }
    public String getVerifierId() { return verifierId; }
    public void setVerifierId(String verifierId) { this.verifierId = verifierId; }
    public String getVerifierName() { return verifierName; }
    public void setVerifierName(String verifierName) { this.verifierName = verifierName; }
}
