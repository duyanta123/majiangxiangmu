package com.ruoyi.system.dto;

public class RevenueStats {
    private String date;
    private Long reservationAmount;
    private Long consumptionAmount;
    private Long totalAmount;

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public Long getReservationAmount() { return reservationAmount; }
    public void setReservationAmount(Long reservationAmount) { this.reservationAmount = reservationAmount; }
    public Long getConsumptionAmount() { return consumptionAmount; }
    public void setConsumptionAmount(Long consumptionAmount) { this.consumptionAmount = consumptionAmount; }
    public Long getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Long totalAmount) { this.totalAmount = totalAmount; }
}
