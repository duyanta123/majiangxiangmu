package com.ruoyi.system.domain;

import java.util.Date;

public class SysAiSchedule {
    private Long scheduleId;
    private Long storeId;
    private String storeName;
    private String date;
    private String timeSlot;
    private String employeeName;
    private String position;
    private Integer workload;
    private String status;
    private String aiSuggestion;
    private Date createTime;
    private Date updateTime;

    public Long getScheduleId() { return scheduleId; }
    public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }
    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }
    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public Integer getWorkload() { return workload; }
    public void setWorkload(Integer workload) { this.workload = workload; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAiSuggestion() { return aiSuggestion; }
    public void setAiSuggestion(String aiSuggestion) { this.aiSuggestion = aiSuggestion; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
