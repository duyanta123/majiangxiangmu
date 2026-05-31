package com.ruoyi.system.dto;

public class MemberStats {
    private String date;
    private Integer newMembers;
    private Integer activeMembers;
    private Integer totalMembers;

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public Integer getNewMembers() { return newMembers; }
    public void setNewMembers(Integer newMembers) { this.newMembers = newMembers; }
    public Integer getActiveMembers() { return activeMembers; }
    public void setActiveMembers(Integer activeMembers) { this.activeMembers = activeMembers; }
    public Integer getTotalMembers() { return totalMembers; }
    public void setTotalMembers(Integer totalMembers) { this.totalMembers = totalMembers; }
}
