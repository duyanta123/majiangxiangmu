package com.ruoyi.system.domain;

import java.util.Date;

public class SysAnalysis {
    private Long id;
    private String analysisType;
    private String periodType;
    private String periodValue;
    private Long storeId;
    private String storeName;
    private Integer totalOrders;
    private Integer completedOrders;
    private Integer cancelledOrders;
    private Long totalRevenue;
    private Long reservationRevenue;
    private Long consumptionRevenue;
    private Integer newMembers;
    private Integer activeMembers;
    private Integer totalPointsEarned;
    private Integer totalPointsUsed;
    private Integer newPlayers;
    private Integer totalReviews;
    private Double avgRating;
    private Integer tableUtilization;
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAnalysisType() { return analysisType; }
    public void setAnalysisType(String analysisType) { this.analysisType = analysisType; }
    public String getPeriodType() { return periodType; }
    public void setPeriodType(String periodType) { this.periodType = periodType; }
    public String getPeriodValue() { return periodValue; }
    public void setPeriodValue(String periodValue) { this.periodValue = periodValue; }
    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }
    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }
    public Integer getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Integer totalOrders) { this.totalOrders = totalOrders; }
    public Integer getCompletedOrders() { return completedOrders; }
    public void setCompletedOrders(Integer completedOrders) { this.completedOrders = completedOrders; }
    public Integer getCancelledOrders() { return cancelledOrders; }
    public void setCancelledOrders(Integer cancelledOrders) { this.cancelledOrders = cancelledOrders; }
    public Long getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(Long totalRevenue) { this.totalRevenue = totalRevenue; }
    public Long getReservationRevenue() { return reservationRevenue; }
    public void setReservationRevenue(Long reservationRevenue) { this.reservationRevenue = reservationRevenue; }
    public Long getConsumptionRevenue() { return consumptionRevenue; }
    public void setConsumptionRevenue(Long consumptionRevenue) { this.consumptionRevenue = consumptionRevenue; }
    public Integer getNewMembers() { return newMembers; }
    public void setNewMembers(Integer newMembers) { this.newMembers = newMembers; }
    public Integer getActiveMembers() { return activeMembers; }
    public void setActiveMembers(Integer activeMembers) { this.activeMembers = activeMembers; }
    public Integer getTotalPointsEarned() { return totalPointsEarned; }
    public void setTotalPointsEarned(Integer totalPointsEarned) { this.totalPointsEarned = totalPointsEarned; }
    public Integer getTotalPointsUsed() { return totalPointsUsed; }
    public void setTotalPointsUsed(Integer totalPointsUsed) { this.totalPointsUsed = totalPointsUsed; }
    public Integer getNewPlayers() { return newPlayers; }
    public void setNewPlayers(Integer newPlayers) { this.newPlayers = newPlayers; }
    public Integer getTotalReviews() { return totalReviews; }
    public void setTotalReviews(Integer totalReviews) { this.totalReviews = totalReviews; }
    public Double getAvgRating() { return avgRating; }
    public void setAvgRating(Double avgRating) { this.avgRating = avgRating; }
    public Integer getTableUtilization() { return tableUtilization; }
    public void setTableUtilization(Integer tableUtilization) { this.tableUtilization = tableUtilization; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
